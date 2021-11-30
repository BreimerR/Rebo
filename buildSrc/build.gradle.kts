plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    mavenLocal()
}

val kotlinVersion = "1.5.31"

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    compileOnly(kotlin("gradle-plugin-api", kotlinVersion))
    compileOnly(files("/home/brymher/.gradle/wrapper/dists/gradle-7.1.1-bin/f29rtwfnc96ub43tt7p47zsru/gradle-7.1.1/lib/gradle-kotlin-dsl-7.1.1.jar"))
    implementation("libetal.gradle.plugins:libBuildSrc:1.5")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinVersion")


}
