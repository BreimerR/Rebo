package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSValueArgument
import kotlin.reflect.KProperty

open class DelegateParameters(arguments: List<KSValueArgument>) {

    private var arguments: List<KSValueArgument>? = arguments

    private val reserve = mutableMapOf<String, Any?>()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> invoke(converter: (Any?) -> T) = TypedDelegateParameter(this, converter)

    open operator fun <R> getValue(receiver: R?, property: KProperty<*>): Any? = reserve[property.name] ?: run {
        arguments?.let {
            it.forEachIndexed { i, argument ->
                argument.name?.asString()?.let { argumentName ->
                    Logger.warn("Adding property ${property.name}  ${ argument.value}")
                    reserve[argumentName] = argument.value
                } ?: Logger.error("Can't resolve argument $i for @PARAM")
            }
        }

        reserve[property.name]
    }

    open operator fun getValue(receiver: Nothing?, property: KProperty<*>): Any? = getValue<Nothing>(receiver, property)
}

