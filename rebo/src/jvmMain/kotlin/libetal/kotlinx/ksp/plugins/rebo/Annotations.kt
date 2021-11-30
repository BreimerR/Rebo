package libetal.kotlinx.ksp.plugins.rebo

object Annotations {

    private const val annotations = "libetal.rebo.annotations.exposed"

    const val Entity = "$annotations.entities.Entity"
    const val Column = "$annotations.columns.Column"
    const val PrimaryKey = "$annotations.properties.PrimaryKey"
    const val ForeignKey = "$annotations.properties.ForeignKey"
    const val MigrationHandler = "$annotations.migrations.MigrationHandler"

}