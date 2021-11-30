package libetal.rebo.annotations.exposed.properties

/**
 * Defines a column as unique
 * On addition on argument on the value is
 * unique if referenced Column && current Column exists in
 * the database
 * */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class Unique(val on: String = "")