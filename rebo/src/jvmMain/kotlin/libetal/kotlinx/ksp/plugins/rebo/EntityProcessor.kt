package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import libetal.kotlinx.ksp.plugins.utils.File
import libetal.kotlinx.ksp.plugins.utils.TopLevelDeclaration

class EntityProcessor(environment: SymbolProcessorEnvironment) : libetal.kotlinx.ksp.plugins.utils.SymbolProcessor(environment) {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val unProcessedEntities = resolver.getSymbolsWithAnnotation(Annotations.Entity)
            .filterIsInstance<KSClassDeclaration>()

        val extensionsConverters = mutableListOf<ExtensionConverter>()

        val dependencies = Dependencies(true, *resolver.getAllFiles().toList().toTypedArray())

        unProcessedEntities.forEach { ksClassDeclaration ->
            val fqName = ksClassDeclaration.qualifiedName?.asString()

            val kClassDeclaration =
                fqName?.let { fullyQualifiedName -> processed[fullyQualifiedName] as? KClassDeclaration } ?: KClassDeclaration(
                    ksClassDeclaration
                )

            with(kClassDeclaration) {
                File(daoName, codeGenerator, dependencies, generatedPackageName).addConverter(
                    DaoConverter(this)
                ).write()

                if (hasPrimaryKey && primaryColumn?.propertyName == "id") File(
                    tableClassName,
                    codeGenerator,
                    dependencies,
                    generatedPackageName
                ).addConverter(
                    TableConverter(this)
                ).write()

                fqName?.let { fullyQualifiedName ->
                    processed[fullyQualifiedName] = this
                }

                extensionsConverters += ExtensionConverter(this)
            }

        }

        File("extensions", codeGenerator, dependencies, "rebo.extensions").apply {
            extensionsConverters.forEach {
                addConverter(it)
            }
        }.write()

        return emptyList()
    }


    companion object {
        val processed = mutableMapOf<String, TopLevelDeclaration<*, *>>()

        fun getDeclaration(fqName: String) = processed[fqName]
    }

}


