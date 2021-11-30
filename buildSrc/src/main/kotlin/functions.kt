import libetal.gradle.add
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun KotlinMultiplatformExtension.targets() {

    jvm()

    js(BOTH) {
        browser()
        nodejs()
    }

    val hostOs = System.getProperty("os.name")

    @Suppress("UNUSED_VARIABLE")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        hostOs.startsWith("Windows") -> mingwX64("native")
        else -> throw  GradleException("Host OS is not supported in Kotlin/Native.")
    }
}


fun createConfigure(project: Project, kotlinVersion: String, exposedVersion: String) =
    Action<NamedDomainObjectContainer<KotlinSourceSet>> {

        val commonMain by getting

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test", kotlinVersion))
            }
        }

        val jvmMain by getting {
            dependencies {
                add(project) {
                    project {
                        +"annotations:common"
                    }
                }
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test", kotlinVersion))
            }
        }

        val nativeMain by getting

        val nativeTest by getting {
            dependencies {
                implementation(kotlin("test", kotlinVersion))
            }
        }

    }

fun KotlinMultiplatformExtension.commonSourceSets(project: Project, kotlinVersion: String, exposedVersion: String): Unit {

    val configure = createConfigure(project, kotlinVersion, exposedVersion)

    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)

}