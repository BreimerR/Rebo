package libetal.rebo.annotations.exposed.entities

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Entity(
    val name: String = "",
    val collate: String = ""
)

