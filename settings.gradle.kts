rootProject.name = "reduce-and-conquer"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":example:androidApp")
project(":example:androidApp").projectDir = file("example/androidApp")

include(":example:desktopApp")
project(":example:desktopApp").projectDir = file("example/desktopApp")

include(":example:shared")
project(":example:shared").projectDir = file("example/shared")

include(":example:webApp")
project(":example:webApp").projectDir = file("example/webApp")

include(":library")
project(":library").projectDir = file("library")