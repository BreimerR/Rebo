package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSClassDeclaration

abstract class ClassDeclaration<T, D : ClassDeclaration<T, D>>(ksDeclaration: KSClassDeclaration) :
    KSClassDeclaration by ksDeclaration,
    TopLevelDeclaration<KSClassDeclaration, D>(ksDeclaration)