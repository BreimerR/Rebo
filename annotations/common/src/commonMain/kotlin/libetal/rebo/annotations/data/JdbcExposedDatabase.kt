package libetal.rebo.annotations.data


/**@Description
 * This is used to annotate an abstract class or interface
 * It requires that the class to have a few fields required by the database
 * configuration class i.e
 *
 * val name:String ="<DATABASE-NAME>"
 * val driver:String ="<DATABASE-DRIVER>"
 * val parameters"Map<String,String> // ?allowDrivers=false....
 *
 * */
@Retention(
    AnnotationRetention.SOURCE
)
@Target(AnnotationTarget.CLASS)
annotation class JdbcExposedDatabase
