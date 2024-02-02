rootProject.name = "hyper-rpc"

include(":common")
include(":api")
include(":impl")
include(":service")

pluginManagement {
    repositories {
        mavenLocal()
        maven {
            name = "aliyun-public"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

buildscript {
    repositories {
        mavenLocal()
        maven {
            name = "aliyun-public"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        mavenCentral()
    }
    dependencies {
        classpath("org.apache.commons:commons-lang3:3.14.0")
    }
}
