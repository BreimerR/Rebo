package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import libetal.kotlinx.ksp.plugins.utils.Converter

class DaoConverter(override var declaration: KClassDeclaration) : Converter<KSClassDeclaration, KClassDeclaration>() {

    private var indent = ""

    private val tableQualifiedName by lazy {
        declaration.tableQualifiedName
    }

    private val daoColumns by lazy {
        var code = ""

        declaration.columns.forEach { column ->
            with(column) {
                val rightHandExpression = if (!isForeign) {
                    "$tableQualifiedName.$propertyName"
                } else {
                    val function = if (isNullable) "optionalReferencedOn" else "referencedOn"

                    daoClass?.let { daoClass ->
                        "${daoClass.daoQualifiedName}  $function $tableQualifiedName.$propertyName"
                    } ?: throw RuntimeException("Can't find daoClass for $fqName : $qualifiedReturnType")
                }

                if (!(isPrimary && propertyName == "id")) {
                    code += """|
                    |${indent}var $propertyName by $rightHandExpression
                """.trimMargin()
                }
            }

        }

        code
    }

    override fun convert(): String = with(declaration) {
        convert()
    }

    private fun KClassDeclaration.convert(): String {
        return if (primaryColumn?.propertyName == "id") {

            indent = "    "

            addImport("org.jetbrains.exposed.dao.Entity")
            addImport("org.jetbrains.exposed.dao.EntityClass")
            addImport("org.jetbrains.exposed.dao.id.EntityID")
            addImport("org.jetbrains.exposed.sql.or")
            addImport("org.jetbrains.exposed.sql.transactions.transaction")

            """|
            |class $daoName(id: $primaryEntityKeyTypeString) : $daoSuperClassWithId(id) {
            |$daoColumns
            |
            |${indent}companion object : EntityClass<Int, $daoName>($tableQualifiedName) {
            |$indent
            |$indent}
            |}
        """.trimMargin()
        } else {
            """|object $daoName : org.jetbrains.exposed.sql.Table(){
            |$columnsCode
            |}
        """.trimMargin()
        }
    }

}