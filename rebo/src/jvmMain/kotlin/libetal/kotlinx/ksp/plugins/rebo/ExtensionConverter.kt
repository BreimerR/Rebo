package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlinx.strings.lcFirst
import kotlinx.strings.ucFirst
import libetal.kotlinx.ksp.plugins.utils.Converter


class ExtensionConverter(override val declaration: KClassDeclaration) : Converter<KSClassDeclaration, KClassDeclaration>() {

    private var indent = "    ";

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
            |fun $fqName.delete(): Boolean {
            |${indent}val $entityClassSimpleNameAsArgName = $daoFqName.first($contextColumnsAsArguments
            |$indent)
            |
            |${indent}return $entityClassSimpleNameAsArgName?.delete() != null
            |}
        """.trimMargin()
        }
    }

    /**
     * public fun Contact.create(): Contact = libetal.rebo.examples.generated.daos.Contacts.insert(this)
     * */
    private val createMethod by lazy {
        with(declaration) {
            """|
            |fun $fqName.create(): $fqName = 
            |$indent$daoFqName.insert(this@create)""".trimMargin()
        }
    }

    /**
     * val Contact.exists: Boolean
     *     get() = libetal.rebo.examples.generated.daos.Contacts.first(this) != null
     * */
    private val existsProperty by lazy {
        with(declaration) {
            """|
            |val $fqName.exists: Boolean
            |${indent}get() = $daoFqName.first(this@exists) != null
        """.trimMargin()
        }
    }

    private val KPropertyDeclaration.rightHandUpdateString
        get() = if (isForeign)
            """${immediateReferenced?.daoFqName}.update(${propertyName.lcFirst})"""
        else propertyName.lcFirst

    private val KPropertyDeclaration.updateString
        get() = """this.$propertyName = $rightHandUpdateString"""

    /**
     * fun Email.update_identifier(identifier: String): Boolean = transaction {
     *     libetal.rebo.examples.generated.daos.Emails.find(this@update_identifier)?.apply {
     *      this.identifier = identifier
     *     }
     *         ?: throw BatchUpdateException(Exception("Object not found in database"))
     * true
    }
     * */
    private val updatePropertiesMethods by lazy {
        with(declaration) {
            columns.filter { !it.isPrimary }.joinToString("\n") {
                val methodName = """update${it.propertyName.ucFirst}"""
                """|
                    |fun $fqName.$methodName(${it.propertyName.lcFirst}: ${it.fqReturnType}): Boolean =
                    |${indent}org.jetbrains.exposed.sql.transactions.transaction {
                    |$indent    $daoFqName.find(this@$methodName)?.apply {
                    |$indent        ${it.updateString}
                    |$indent    }
                    |$indent        ?: throw java.sql.BatchUpdateException(Exception("Object not found in database"))
                    |$indent    true
                    |$indent}
                """.trimMargin()
            }
        }
    }

    override fun convert(): String = """|$existsProperty
        |$createMethod
        |$deleteMethod${declaration.noUpdatePropertiesAnnotation?.let { "" } ?: updatePropertiesMethods}
    """.trimMargin()

}