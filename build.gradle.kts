import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    kotlin("jvm").version(System.getProperty("kotlin_version"))
}

base { archivesName.set(project.extra["archives_base_name"] as String) }

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

val versionMinecraft = project.extra["minecraft_version"] as String
val versionJava = project.extra["java_version"] as String
val versionLoader = project.extra["loader_version"] as String
val versionFabricApi = project.extra["fabric_version"] as String
val versionFabricKotlin = project.extra["fabric_language_kotlin_version"] as String

dependencies {
    minecraft("com.mojang", "minecraft", versionMinecraft)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, null, "v2")
    modImplementation("net.fabricmc", "fabric-loader", versionLoader)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", versionFabricApi)
    modImplementation("net.fabricmc", "fabric-language-kotlin", versionFabricKotlin)
}

tasks {
    val javaVersion = JavaVersion.toVersion(versionJava.toInt())
    val javaVersionString = javaVersion.toString()

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersionString
        targetCompatibility = javaVersionString
        options.release.set(javaVersionString.toInt())
    }

    withType<KotlinCompile> { kotlinOptions { jvmTarget = javaVersionString } }

    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersionString)) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }

    jar { from("LICENSE") { rename { "${it}_${base.archivesName.get()}" } } }
    processResources { filesMatching("fabric.mod.json") { expand(mapOf("version" to version)) } }
}

/* Data Generation */

val generatedResourcesDir = "src/main/generated"

loom {
    runs {
        create("Data Generation") {
            server()

            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file(generatedResourcesDir)}")
            vmArg("-Dfabric-api.datagen.modid=${project.extra["mod_id" as String]}")

            runDir("build/datagen")
        }
    }
}

sourceSets.main {
    resources {
        srcDir(generatedResourcesDir)
    }
}
