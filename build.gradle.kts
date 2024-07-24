import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val javaVersion = JavaVersion.VERSION_21
val loaderVersion: String by project
val mavenGroup: String by project
val minecraftVersion: String by project

plugins {
    id("fabric-loom")
    kotlin("jvm")
}

loom {
    accessWidenerPath = file("src/main/resources/learnenglish.accesswidener")
}

repositories {
    maven("https://api.modrinth.com/maven") {
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft("com.mojang", "minecraft", minecraftVersion)

    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")

    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)

    modImplementation("maven.modrinth", "language-reload", "1.6.1+1.21")

    modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.100.7+1.21")

    val modMenuBadgesLibVersion: String by project
    include(modImplementation("maven.modrinth", "modmenu-badges-lib", modMenuBadgesLibVersion))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions { 
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    jar {
        from("LICENSE")
    }

    processResources {
        val modVersion: String by project
        val modName: String by project

        filesMatching("fabric.mod.json") {
            expand(
                mutableMapOf(
                    "modName" to modName,
                    "modVersion" to modVersion,
                    "minecraftVersion" to minecraftVersion,
                    "javaVersion" to javaVersion.toString()
                )
            )
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
        }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
