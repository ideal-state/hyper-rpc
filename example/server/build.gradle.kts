import org.apache.commons.lang3.time.DateFormatUtils
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version ("8.+")
}

val authors = project.ext["authors"] as String
val javaVersion = project.ext["javaVersion"] as Int
val charset = project.ext["charset"] as String

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly(fileTree("${projectDir}/libraries"))

    api(project(":example:common"))
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
    val assetsDir = "assets/${rootProject.name.lowercase()}/${project.name.lowercase()}"
    eachFile {
        if (path.startsWith("assets/")) {
            print("$path >> ")
            path = assetsDir + path.substring(6)
            println(path)
        }
    }
}

tasks.shadowJar {
    val projectName = "${rootProject.name}-${project.name}"
    archiveBaseName.set(projectName)
    archiveClassifier.set("")
    from(project(":example:common").tasks.shadowJar)
    manifest {
        attributes(linkedMapOf(
                "Group" to project.group,
                "Name" to projectName,
                "Version" to project.version,
                "Authors" to authors,
                "Updated" to DateFormatUtils.format(Date(), "yyyy-MM-dd HH:mm:ssZ"),
                "Multi-Release" to true,
                "Main-Class" to "team.idealstate.hyper.rpc.example.server.ExampleServer",
        ))
    }
}

tasks.jar {
    dependsOn(tasks.shadowJar)
    enabled = false
    finalizedBy("copyStartScriptToBuildLibs")
}

tasks.create<Copy>("copyStartScriptToBuildLibs") {
    from("${projectDir}/start.bat")
    into("${projectDir}/build/libs")
    filteringCharset = charset
    val props = mapOf(
            "java" to javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
                vendor.set(JvmVendorSpec.AZUL)
            }.get().executablePath.asFile.absolutePath,
            "version" to version.toString(),
    )
    filesMatching("start.bat") {
        expand(props)
    }
}
