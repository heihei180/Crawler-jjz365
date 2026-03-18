
// 添加插件仓库配置
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()

    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "Crawler-jjz365"

