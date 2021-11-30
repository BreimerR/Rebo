package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.libetal.lazy.contexed.contexedLazy
import libetal.kotlinx.ksp.plugins.utils.Converter

class TableConverter(override var declaration: KClassDeclaration) : Converter<KSClassDeclaration, KClassDeclaration>() {

    init {
        // addImport("org.jetbrains.exposed.dao.id.EntityID")
        addImport("org.jetbrains.exposed.dao.id.IdTable")
        // addImport("org.jetbrains.exposed.sql.Column")
        // addImport("org.jetbrains.exposed.sql.ReferenceOption")
        // addImport("org.jetbrains.exposed.sql.Table")
    }

    override fun convert(): String = with(declaration) {

        """object $tableClassName : IdTable<$primaryKeyTypeString>("$tableName"){
            |$columnsCode
            |} """.trimMargin()
    }

}


