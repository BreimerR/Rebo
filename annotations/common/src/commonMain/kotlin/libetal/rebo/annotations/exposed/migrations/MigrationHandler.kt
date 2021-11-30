package libetal.rebo.annotations.exposed.migrations

/**
 * Int used to determine upgrade or downgrading
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class MigrationHandler(val fromVersion: Int, val toVersion: Int)