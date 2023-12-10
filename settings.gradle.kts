pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials.username = "mapbox"
            // Use the secret token stored in gradle.properties as the password
            credentials.password = "sk.eyJ1IjoibXR0Y2hwbW4iLCJhIjoiY2xwczRuMnMzMDA1ZTJubjRnZW5hdG51OSJ9.z47w6ldLy9OemuVZyTZeKQ"
            authentication.create<BasicAuthentication>("basic")
        }
    }
}

rootProject.name = "RANGR"
include(":app")

