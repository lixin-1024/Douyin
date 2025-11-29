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
        // 腾讯云Google镜像
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/google/") }
        // 腾讯云Maven中央仓库镜像
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/") }
    }
}

rootProject.name = "Douyin"
include(":app")