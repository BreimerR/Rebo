import libetal.gradle.implementations.types.Group
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import libetal.gradle.managers.KotlinDependenciesManager

typealias KotlinGroup = Group<KotlinDependencyHandler, KotlinDependenciesManager, KotlinDependenciesManager>


fun KotlinGroup.exposed(action: KotlinGroup.() -> Unit) =
    "exposed" {
        action()
    }


fun KotlinGroup.kotlinx(action: KotlinGroup.() -> Unit) =
    "kotlinx"{
        action()
    }

