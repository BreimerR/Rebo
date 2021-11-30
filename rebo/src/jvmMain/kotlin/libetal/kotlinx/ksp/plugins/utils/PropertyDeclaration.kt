package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration

abstract class PropertyDeclaration<T, D : PropertyDeclaration<T, D>>(ksDeclaration: KSPropertyDeclaration) :
    KSPropertyDeclaration by ksDeclaration,
    TopLevelDeclaration<KSPropertyDeclaration, D>(ksDeclaration)

