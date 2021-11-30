package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSDeclaration

abstract class Converter<T : KSDeclaration, D : TopLevelDeclaration<T, D>> {

    abstract val declaration: D

    val imports = mutableListOf<String>()

    abstract fun convert(): String

    fun addImport(import: String) = imports.add(import)

}