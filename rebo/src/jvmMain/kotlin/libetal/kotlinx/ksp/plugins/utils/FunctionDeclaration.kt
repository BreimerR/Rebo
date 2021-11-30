package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

abstract class FunctionDeclaration<T, D : FunctionDeclaration<T, D>>(ksDeclaration: KSFunctionDeclaration) :
    KSFunctionDeclaration by ksDeclaration,
    TopLevelDeclaration<KSFunctionDeclaration, D>(ksDeclaration)
