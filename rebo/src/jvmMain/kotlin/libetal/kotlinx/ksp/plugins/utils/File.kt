package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSDeclaration
import java.io.OutputStreamWriter

class File(
    private val fileName: String,
    private val codeGenerator: CodeGenerator,
    private val dependencies: Dependencies,
    private val packageName: String = "",
    private val extensionName: String = "kt",
) {


    private val converters = mutableListOf<Converter<*, *>>()

    fun <K : KSDeclaration, D : TopLevelDeclaration<K, D>, C : Converter<K, D>> addConverter(converter: C): File {
        converters += converter
        return this
    }

    /**
     * Create The file and write converted code to the file.
     * */
    fun write() {
        val outputStream = try {
            codeGenerator.createNewFile(dependencies, packageName, fileName, extensionName)
        } catch (e: Exception) {
            Logger.warn("TODO: File probably already exist don't know how to handle it yet. ")
            null
        }

        outputStream?.use { out ->
            OutputStreamWriter(out).use { writer ->
                writer.write("package $packageName\n\n")
                var imports = ""
                var secondLevel = ""
                converters.forEach { converter ->
                    secondLevel += "\n\n" + converter.convert()
                    imports += converter.imports.joinToString("\n") { "import $it" }
                }

                writer.write(imports)
                writer.write(secondLevel)
            }

        }
    }

}


