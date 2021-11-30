rootProject.name = "Rebo"

pluginManagement {

    resolutionStrategy {

        this.eachPlugin {
            val kotlinVersion: String by settings
            val symbolProcessingVersion: String by settings

            val requested = this.requested

            if (requested.id.id == "com.google.devtools.ksp") {
                useVersion("$kotlinVersion-$symbolProcessingVersion")
            }

        }

    }

}

include(":annotations:common")
include(":rebo")
include(":test")
