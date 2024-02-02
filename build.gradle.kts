import java.util.*

val properties = Properties()
file("${projectDir}/local.properties").inputStream().use {
    properties.load(it)
}
val majorVersion = properties["self.version.major"] as String
val authors = properties["self.authors"] as String
val javaVersion = (properties["self.java.version"] as String).toInt()
val charset = properties["self.charset"] as String

subprojects {

    group = "team.idealstate.hyper.rpc"
    version = majorVersion

    apply {
        plugin("java")
        plugin("java-library")
    }

    repositories {
        mavenLocal()
        maven {
            name = "aliyun-public"
            url = uri("https://maven.aliyun.com/repository/public")
        }
        mavenCentral()
    }

    ext["authors"] = authors
    ext["javaVersion"] = javaVersion
    ext["charset"] = charset
}

tasks.create<Delete>("clean") {
    delete("${projectDir}/build")
}
