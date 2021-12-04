package libetal.kotlinx.ksp.plugins.utils

abstract class BaseConverter {
    val imports: MutableList<String> = mutableListOf<String>()
    abstract fun convert(): String
}