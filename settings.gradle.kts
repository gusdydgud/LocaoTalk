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
        maven { url = uri("https://naver.jfrog.io/artifactory/maven/") } // 네이버 지도 SDK 저장소
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://repository.map.naver.com/archive/maven") } // 네이버 지도 SDK 저장소
    }
}

rootProject.name = "Locaotalk"
include(":app")
