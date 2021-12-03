package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.strings.lcFirst
import libetal.kotlinx.ksp.plugins.utils.Converter

class ExtensionConverter(override val declaration: KClassDeclaration) : Converter<KSClassDeclaration, KClassDeclaration>() {

    var indent = "    ";

    private val entityClassSimpleName by lazy {
        declaration.simpleName.asString()
    }

    private val contextColumnsAsArguments by lazy {
        declaration.columns.joinToString(",") {
            """|
                |$indent    ${it.propertyName} = this@delete.${it.propertyName}""".trimMargin()
        }
    }

    private val entityClassSimpleNameAsArgName by lazy {
        entityClassSimpleName.lcFirst
    }

    /**public fun Contact.delete(): Boolean = transaction {
     *     val contact = libetal.rebo.examples.generated.daos.Contacts.first(
     *         this@delete.id,
     *         this@delete.country,
     *         this@delete.email
     *     )
     *     contact?.delete() != null
     * }
     * */
    private val deleteMethod by lazy {
        with(declaration) {
            """|
            |fun $fqName.delete(){
            |${indent}val $entityClassSimpleNameAsArgName = $daoFqName.first($contextColumnsAsArguments
            |$indent)
            |$indent$entityClassSimpleNameAsArgName?.delete() != null
            |}
        """.trimMargin()
        }
    }


    override fun convert(): String = """|
        |$deleteMethod
    """.trimMargin()

}