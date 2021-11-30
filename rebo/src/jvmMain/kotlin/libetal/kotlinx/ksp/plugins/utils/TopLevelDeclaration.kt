package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSDeclaration
import libetal.kotlinx.ksp.plugins.rebo.KClassDeclaration

abstract class TopLevelDeclaration<T : KSDeclaration, D : TopLevelDeclaration<T, D>>(val declaration: T) {

}

