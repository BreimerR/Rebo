import libetal.gradle.implementations.types.Group
import libetal.gradle.managers.KotlinDependenciesManager
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler


fun KotlinDependenciesManager.exposed(version: String) =
    jetbrains {
        exposed {
            "spring-transaction"(version)
            exposed(version) {
                +"dao"
                +"jdbc"
                +"core"
                "java"{
                    +"time"
                }
            }
        }
    }


fun KotlinDependenciesManager.addSymbolProcessing(kotlinVersion: String, symbolProcessingVersion: String) {
    "com.google.devtools.ksp"{
        "symbol-processing-api"("$kotlinVersion-$symbolProcessingVersion")
    }
}

fun KotlinDependenciesManager.logBackClassic(version: String) {
    "ch.qos.logback"(version) {
        +"logback-classic"
    }
}

fun KotlinDependenciesManager.jetbrains(action: Group<KotlinDependencyHandler, KotlinDependenciesManager, KotlinDependenciesManager>.() -> Unit) =
    "org.jetbrains"{
        action()
    }

fun KotlinDependenciesManager.local(version: String) {
    "kotlinx"(version) {
        +"lazy"
        +"strings"
        +"languages"
    }
}

typealias DependencyGroup = Group<KotlinDependencyHandler, KotlinDependenciesManager, KotlinDependenciesManager>

fun KotlinDependenciesManager.gitHub(
    prefix: String = "io",
    action: DependencyGroup.() -> Unit
) =
    "$prefix.github"{
        action()
    }


fun KotlinDependenciesManager.kotlinx(action: Group<KotlinDependencyHandler, KotlinDependenciesManager, KotlinDependenciesManager>.() -> Unit) =
    "kotlinx"{
        action()
    }


fun KotlinDependenciesManager.libetal(action: Group<KotlinDependencyHandler, KotlinDependenciesManager, KotlinDependenciesManager>.() -> Unit) =
    "libetal"{
        action()
    }