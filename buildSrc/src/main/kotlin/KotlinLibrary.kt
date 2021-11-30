import libetal.gradle.implementations.types.Group
import libetal.gradle.implementations.types.Library
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import libetal.gradle.managers.KotlinDependenciesManager

typealias KotlinLibrary =  Library<KotlinDependencyHandler, KotlinDependenciesManager, KotlinDependenciesManager>

fun KotlinGroup.exposed(version: String, action: KotlinLibrary.() -> Unit) =
    "exposed"('-', version) {
        action()
    }