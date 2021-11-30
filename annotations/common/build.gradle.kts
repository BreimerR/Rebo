import libetal.gradle.add

val projectGroup: String by project
val projectVersion: String by project
val kotlinVersion: String by project
val jvmTarget: String by project

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

group = "$projectGroup.rebo.annotations"
version = "$projectVersion.${kotlinVersion.replace('.', ' ')}-SNAPSHOT"

kotlin {

    targets()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test", kotlinVersion))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = jvmTarget
}


tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
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
