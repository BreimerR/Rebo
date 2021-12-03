import libetal.gradle.add
import libetal.gradle.managers.KotlinDependenciesManager

val jvmTarget: String by project
val projectGroup: String by project
val kotlinVersion: String by project
val projectVersion: String by project
val symbolProcessingVersion: String by project

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = projectGroup
version = "$projectVersion.${kotlinVersion.replace(".", "")}-SNAPSHOT"


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = jvmTarget
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                add(project) {
                    addSymbolProcessing(kotlinVersion, symbolProcessingVersion)
                    "kotlinx"("1.0.0") {
                        +"lazy"
                        +"strings"
                        +"languages"
                    }

                    project{
                        +"annotations:common"
                    }
                }
            }
        }
        val jvmTest by getting

    }
}
