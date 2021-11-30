package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

abstract class SymbolProcessor(private val environment: SymbolProcessorEnvironment) :
    com.google.devtools.ksp.processing.SymbolProcessor {

    init {
        Logger.logger = environment.logger
        options = environment.options
    }

    protected val codeGenerator by lazy {
        environment.codeGenerator
    }


    companion object {

        lateinit var options: Map<String, String>

        private fun getOption(name: String) = options[name]

        fun getOption(name: String, default: String) = getOption(name) ?: default

        fun <T> getOption(name: String, converter: (String?) -> T) = converter(getOption(name))
    }
}

