package libetal.kotlinx.ksp.plugins.rebo

import com.google.devtools.ksp.symbol.KSClassDeclaration
import libetal.kotlinx.ksp.plugins.utils.Converter

class ExtensionConverter(override val declaration: KClassDeclaration) : Converter<KSClassDeclaration, KClassDeclaration>() {
    override fun convert(): String = """fun ${declaration.daoName}(){
        |}
    """.trimMargin()

}