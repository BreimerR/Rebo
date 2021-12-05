package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSDeclaration

abstract class Converter<T : KSDeclaration, D : TopLevelDeclaration<T, D>> : BaseConverter() {

    abstract val declaration: D

}

