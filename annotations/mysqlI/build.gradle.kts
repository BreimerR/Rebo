import libetal.kapt.build.common.annotations.commonSourceSets
import libetal.kapt.build.common.annotations.targets
import org.jetbrains.dokka.gradle.DokkaTask

val jvmTarget: String by project
val projectGroup: String by project
val kotlinVersion: String by project
val exposedVersion: String by project
val projectVersion: String by project

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = projectGroup
version = projectVersion

kotlin {

    targets()

    commonSourceSets(project)

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = jvmTarget
}


tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {

        register("customSourceSet") {
            this.jdkVersion.set(jvmTarget.toInt())
            this.displayName.set("MultiplatformAnnotations")
            this.sourceRoots.from(
                file("src/commonMain/kotlin"),
                file("src/jvmMain/kotlin")
            )
        }
    }
}