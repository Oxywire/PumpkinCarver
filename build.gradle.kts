plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.oxywire"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.citizensnpcs.co/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("net.citizensnpcs:citizensapi:2.0.28-SNAPSHOT")
    implementation("cloud.commandframework:cloud-paper:1.5.0")
    implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")
}

tasks {
    shadowJar {
        listOf(
            "cloud.commandframework",
            "io.leangen.geantyref",
            "net.kyori.adventure.text.minimessage"
        ).forEach { relocate(it, "com.oxywire.pumpkincarver.libs.$it") }
        minimize()
        archiveFileName.set("PumpkinCarver.jar")
    }

    jar {
        archiveFileName.set("PumpkinCarver-noshade.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        expand("version" to project.version)
    }
}
