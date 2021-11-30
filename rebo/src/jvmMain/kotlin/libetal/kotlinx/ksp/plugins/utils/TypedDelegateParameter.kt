package libetal.kotlinx.ksp.plugins.utils

import kotlin.reflect.KProperty

class TypedDelegateParameter<T>(private val arguments: DelegateParameters, val converter: (Any?) -> T) {

    operator fun getValue(receiver: Nothing?, property: KProperty<*>): T =
        converter(arguments.getValue(receiver, property))


    operator fun <R> getValue(receiver: R?, property: KProperty<*>): T =
        converter(arguments.getValue<R>(receiver, property))


}