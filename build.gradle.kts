val jvmTarget: String by project
val projectGroup: String by project
val kotlinVersion: String by project
val projectVersion: String by project
val symbolProcessingVersion: String by project


buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }

    val kotlinVersion: String by project

    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}