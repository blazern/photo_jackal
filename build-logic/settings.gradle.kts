rootProject.name = "build-logic"

pluginManagement {
    repositories {
        maven { url = uri("") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("") }
        google()
        mavenCentral()
    }
}

include(":plugins")
