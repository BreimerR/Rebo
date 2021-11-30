package libetal.rebo.annotations.exposed.enums;

enum class ReferenceOption(val option: String) {
    CASCADE("CASCADE"),
    SET_NULL("SET_NULL"),
    RESTRICT("RESTRICT"),
    NO_ACTION("NO_ACTION");

    override fun toString(): String {
        return option
    }
}