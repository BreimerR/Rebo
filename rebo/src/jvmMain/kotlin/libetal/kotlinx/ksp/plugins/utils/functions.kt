package libetal.kotlinx.ksp.plugins.utils

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSValueArgument
import java.io.OutputStreamWriter

fun KSAnnotated.getAnnotation(annotation: String): KSAnnotation? =
    annotations.firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == annotation }

fun <R> KSAnnotated.useAnnotation(annotation: String, utilize: (KSAnnotation) -> R) = getAnnotation(annotation)?.let(utilize)

fun KSAnnotated.useFirst(vararg consumers: Pair<String, (KSAnnotation) -> Unit>) {
    for ((annotation, consumer) in consumers) {
        if (useAnnotation(annotation, consumer) == Unit) break
    }
}


operator fun Sequence<KSAnnotation>.get(annotationName: String) =
    firstOrNull { it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName }


operator fun List<KSValueArgument>.get(argumentName: String) = firstOrNull { it.name?.asString() == argumentName }

fun OutputStreamWriter.addImport(import: String) = this + "import $import"

operator fun OutputStreamWriter.plusAssign(string: String) {
    write(string)
}

operator fun OutputStreamWriter.plus(line: String) {
    write("\n$line")
}