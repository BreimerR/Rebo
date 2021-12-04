package libetal.kotlinx.ksp.plugins.rebo.visitors

import libetal.kotlinx.ksp.plugins.rebo.EntityProcessor
import libetal.kotlinx.ksp.plugins.rebo.KClassDeclaration
import libetal.kotlinx.ksp.plugins.utils.BaseConverter

/*
*
* import io.ktor.application.Application
import kotlin.Unit
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

public fun Application.reboTablesInit(): Unit {
  transaction{
      SchemaUtils.create(libetal.rebo.examples.generated.tables.ContactsTable)
      SchemaUtils.create(libetal.rebo.examples.generated.tables.UsersTable)
      SchemaUtils.create(libetal.rebo.examples.generated.tables.ProvidersTable)
      SchemaUtils.create(libetal.rebo.examples.generated.tables.EmailsTable)
      SchemaUtils.create(libetal.rebo.examples.generated.tables.AccountsTable)
      SchemaUtils.create(libetal.rebo.examples.generated.tables.CountriesTable)
  }
}
*/

object DatabaseCreatorsConverter : BaseConverter() {

    val declarations = mutableListOf<KClassDeclaration>()

    private val tableInitializers by lazy {
        declarations.joinToString("") {
            """|
                |        org.jetbrains.exposed.sql.SchemaUtils(${it.tableQualifiedName})
            """.trimMargin()
        }
    }

    override fun convert(): String {
        return """|
            |fun io.ktor.application.Application.reboTablesInit(): Unit{
            |    org.jetbrains.exposed.sql.transactions.transaction {$tableInitializers
            |    }
            |}
        """.trimMargin()
    }

}