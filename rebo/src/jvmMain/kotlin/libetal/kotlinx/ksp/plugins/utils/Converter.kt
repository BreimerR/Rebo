package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSDeclaration

abstract class Converter<T : KSDeclaration, D : TopLevelDeclaration<T, D>> : BaseConverter() {

    abstract val declaration: D

    fun addImport(import: String) = imports.add(import)

}

