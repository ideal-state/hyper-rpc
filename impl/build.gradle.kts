import org.apache.commons.lang3.time.DateFormatUtils
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version ("8.+")
}

val projectName = project.ext["projectName"] as String
val authors = project.ext["authors"] as String
val javaVersion = project.ext["javaVersion"] as Int
val charset = project.ext["charset"] as String

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly(fileTree("${projectDir}/libraries"))

    api(project(":api"))
    val jacksonVersion = "2.15.3"
    api("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
    api("com.fasterxml.jackson.module:jackson-module-parameter-names:${jacksonVersion}")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${jacksonVersion}")
    api("io.netty:netty-codec:4.1.86.Final")
    api("io.netty:netty-handler:4.1.59.Final")
}

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = sourceCompatibility
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
        vendor.set(JvmVendorSpec.AZUL)
    }
}

tasks.compileJava {
    options.encoding = charset
}

tasks.processResources {
    filteringCharset = charset
    includeEmptyDirs = false
    val assetsDir = "assets/${projectName}"
    eachFile {
        if (path.startsWith("assets/")) {
            print("$path >> ")
            path = assetsDir + path.substring(6)
            println(path)
        }
    }
}

tasks.shadowJar {
    archiveBaseName.set(projectName)
    archiveClassifier.set("")
    from(project(":api").tasks.shadowJar)
    manifest {
        attributes(linkedMapOf(
                "Group" to project.group,
                "Name" to projectName,
                "Version" to project.version,
                "Authors" to authors,
                "Updated" to DateFormatUtils.format(Date(), "yyyy-MM-dd HH:mm:ssZ"),
                "Multi-Release" to true,
                "Main-Class" to "team.idealstate.hyper.rpc.impl.RsaKeyPairGenerator",
        ))
    }
}

tasks.jar {
    dependsOn(tasks.shadowJar)
    enabled = false
    finalizedBy("copyToRootBuildLibs")
}

tasks.create<Copy>("copyToRootBuildLibs") {
    from(tasks.shadowJar)
    into("${rootProject.projectDir}/build/libs")
}
