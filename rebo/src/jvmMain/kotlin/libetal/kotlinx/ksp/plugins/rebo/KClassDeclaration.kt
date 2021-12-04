package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.libetal.lazy.mutable.mutableLazy
import kotlinx.languages.english.plural
import kotlinx.strings.camelSnakeSplit
import libetal.kotlinx.ksp.plugins.utils.*
import libetal.rebo.annotations.exposed.entities.NoUpdateProperties


class KClassDeclaration(
    delegate: KSClassDeclaration,
) : ClassDeclaration<KClassDeclaration, KClassDeclaration>(delegate) {

    private val stringPackageName by lazy {
        packageName.asString()
    }

    val generatedPackageName by lazy {
        val useGeneratedPackageName = SymbolProcessor.getOption("useGeneratedPackageName") {
            it?.toBoolean() ?: false
        }

        if (useGeneratedPackageName)
            "${stringPackageName}.generated"
        else stringPackageName
    }


    private val isProcessed
        get() = qualifiedName?.asString() in EntityProcessor.processed


    private val name: String by lazy {
        simpleName.asString()
    }


    val daoName by lazy {
        name.camelSnakeSplit.joinToString("") { it.plural }
    }

    @Deprecated("Replaced with daoFqName", ReplaceWith("daoFqName"))
    val daoQualifiedName by lazy {
        "$generatedPackageName.$daoName"
    }

    val daoFqName by lazy {
        "$generatedPackageName.$daoName"
    }

    val fqName by lazy {
        qualifiedName?.asString()
    }

    val tableClassName by lazy {
        "${name}Table"
    }

    // TODO add Support for this
    private val isView by lazy {
        false
    }

    private val nameSplat by lazy {
        name.camelSnakeSplit
    }

    private val nameSnakeCase by lazy {
        nameSplat.joinToString("_")
    }

    val tableName by lazy {
        "${nameSnakeCase.lowercase()}_${if (isView) "view" else "table"}"
    }

    private val tableClassPackageName by lazy {
        generatedPackageName
    }

    val tableQualifiedName by lazy {
        "$tableClassPackageName.$tableClassName"
    }


    var columns by mutableLazy {
        val columns = mutableListOf<KPropertyDeclaration>()
        declarations.forEach {
            if (it.isColumn)
                if (it is KSPropertyDeclaration)
                    columns += KPropertyDeclaration(it) else Logger.error("Somehow property${qualifiedName?.asString()} is annotated with @${Annotations.Column}")
        }

        columns.toList()
    }

    val hasPrimaryKey by lazy {
        columns.firstOrNull { it.isPrimary } != null
    }

    private val KSDeclaration.isColumn
        get() = columnAnnotation != null

    private val KSDeclaration.columnAnnotation
        get() = getAnnotation(Annotations.Column)

    val noUpdatePropertiesAnnotation by lazy {
        getAnnotation(Annotations.NoUpdateProperties)
    }

    var primaryColumn by mutableLazy {
        columns.firstOrNull { it.isPrimary }
    }

    val primaryKeyTypeString by lazy {
        primaryColumn?.primitiveTypeSimpleString
    }

    val primaryEntityKeyTypeString by lazy {
        primaryColumn?.entityKeyTypeString
    }

    val daoSuperClassWithId by lazy {
        "Entity<$primaryKeyTypeString>"
    }

    val columnsCode by lazy {
        var code = ""

        for (column in columns) {
            code += column.tablePropertyString("    ")
        }

        code
    }


}
