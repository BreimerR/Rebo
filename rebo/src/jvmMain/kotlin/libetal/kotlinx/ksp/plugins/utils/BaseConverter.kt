package libetal.kotlinx.ksp.plugins.utils

abstract class BaseConverter {
    val imports = mutableSetOf<String>()
    abstract fun convert(): String
    fun addImport(import: String) = imports.add(import)
}