package libetal.rebo.annotations.exposed.properties

/**
 * Nullable can be used remove feature
 * if the primary column is foreign it
 * could cause field conflicts
 * @WARNING DO NOT USE AnnotationTarget.VALUE_PARAMETER
 * */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class PrimaryKey(val autoIncrement: Boolean = true)





