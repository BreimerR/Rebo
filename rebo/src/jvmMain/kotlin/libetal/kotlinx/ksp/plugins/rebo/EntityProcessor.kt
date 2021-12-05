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

        val dependencies = Dependencies(true, *resolver.getAllFiles().toList().toTypedArray())

        unProcessedEntities.forEach { ksClassDeclaration ->
            val fqName = ksClassDeclaration.qualifiedName?.asString()

            val kClassDeclaration =
                fqName?.let { fullyQualifiedName -> getDeclaration(fullyQualifiedName) as? KClassDeclaration }
                    ?: KClassDeclaration(
                        ksClassDeclaration
                    )

            with(kClassDeclaration) {
                File(daoName, codeGenerator, dependencies, generatedPackageName).addConverter(
                    DaoConverter(this)
                ).write()

                if (hasPrimaryKey) {

                    File(
                        tableClassName,
                        codeGenerator,
                        dependencies,
                        generatedPackageName
                    )
                        .addConverter(TableConverter(this))
                        .write()

                    File(
                        simpleName.asString(),
                        codeGenerator,
                        dependencies,
                        "$generatedPackageName.rebo.extensions"
                    )
                        .addConverter(ExtensionConverter(this))
                        .write()

                    processed += this
                }

            }

        }

        File("reboTablesInit", codeGenerator, dependencies, "rebo.extensions").apply {
            addConverter(DatabaseCreatorsConverter)
        }.write()

        return emptyList()
    }


    companion object {
        val processed = mutableListOf<TopLevelDeclaration<*, *>>()

        fun getDeclaration(fqName: String) = processed.firstOrNull {
            when (it) {
                is KClassDeclaration -> it.fqName == fqName
                else -> false
            }
        }
    }

}
