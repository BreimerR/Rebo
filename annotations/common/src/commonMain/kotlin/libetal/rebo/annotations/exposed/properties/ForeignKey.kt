package libetal.rebo.annotations.exposed.properties

import libetal.rebo.annotations.exposed.enums.ReferenceOption

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class ForeignKey(
    val name: String = "",
    val referencedFiledName: String = "",
    val onDelete: ReferenceOption = ReferenceOption.NO_ACTION,
    val onUpdate: ReferenceOption = ReferenceOption.NO_ACTION,
    val safeInsert: Boolean = false
)


