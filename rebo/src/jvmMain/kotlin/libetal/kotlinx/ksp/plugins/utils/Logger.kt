package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.processing.KSPLogger

object Logger {
    lateinit var logger: KSPLogger

    @Suppress("unused")
    fun logging(message: String, symbol: KSNode? = null) = logger.logging(message, symbol)

    @Suppress("unused")
    fun info(message: String, symbol: KSNode? = null) = logger.info(message, symbol)

    @Suppress("unused")
    fun warn(message: String, symbol: KSNode? = null) = logger.warn(message, symbol)

    fun error(message: String, symbol: KSNode? = null) = logger.error(message, symbol)

    @Suppress("unused")
    fun error(message: Any?, symbol: KSNode? = null) = error(message?.toString() ?: "Can't Resolve error message", symbol)

}