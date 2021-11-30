@file:Suppress("UNUSED_VARIABLE")

import libetal.gradle.add
import libetal.gradle.srcDir

val jvmTarget: String by project
val exposedVersion: String by project
val commonAnnotations: String by project

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")

}

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = jvmTarget
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js {
        browser()
        nodejs()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(commonAnnotations))
            }
        }
        val commonTest by getting
        val jsMain by getting
        val jsTest by getting
        val jvmMain by getting {
            dependencies {
                add(project) {
                    exposed(exposedVersion)
                }
            }
            srcDir("build/generated/ksp/jvmMain/kotlin")
        }
        val jvmTest by getting {
            srcDir("build/generated/ksp/jvmTest/kotlin")
        }
        val nativeMain by getting
        val nativeTest by getting
    }


}


dependencies {
    add("kspJvm", project(":rebo")){

    }
    add("kspJvmTest", project(":rebo"))
    /**TODO
     * Once a database library that is cross platform exists
     * we can add this dependencies here
     * Although
     * add("kspJs", project(":rebo"))
     * add("kspJsTest", project(":rebo"))
     * add("kspNative", project(":rebo"))
     * add("kspNativeTest", project(":rebo"))*/

}