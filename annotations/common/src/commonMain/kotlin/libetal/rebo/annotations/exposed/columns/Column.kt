package libetal.rebo.annotations.exposed.columns


@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class Column(
    val name: String = "",
    val default: String = "",
    val size: Int = 0,
    val collate: String = "",
    val uniqueOn: String = "",
    val unique: Boolean = false,
    val primary: Boolean = false,
    val eagerLoading: Boolean = false,
    val autoIncrement: Boolean = false
)