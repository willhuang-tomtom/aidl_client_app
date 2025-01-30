pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        maven {
            url = uri("https://repositories.tomtom.com/artifactory/maven-dev")
            credentials {
                username = "user_name"
                password = "password"
            }
        }

        google()
        mavenCentral()
    }
}

rootProject.name = "My Application"
include(":app")
