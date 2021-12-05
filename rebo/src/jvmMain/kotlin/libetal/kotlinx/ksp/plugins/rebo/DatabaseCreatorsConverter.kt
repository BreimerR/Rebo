package libetal.kotlinx.ksp.plugins.rebo

import libetal.kotlinx.ksp.plugins.utils.BaseConverter

object DatabaseCreatorsConverter : BaseConverter() {

    private val declarations
        get() = EntityProcessor.processed.filterIsInstance<KClassDeclaration>()

    private val tableInitializers by lazy {
        declarations.joinToString("") {
            """|
                |        SchemaUtils.create(${it.tableFqName})
            """.trimMargin()
        }
    }

    override fun convert(): String {
        addImport("org.jetbrains.exposed.sql.SchemaUtils")
        addImport("io.ktor.application.Application")
        addImport("org.jetbrains.exposed.sql.transactions.transaction")

        return """|
            |fun Application.reboTablesInit(): Unit{
            |    transaction {$tableInitializers
            |    }
            |}
        """.trimMargin()
    }

}