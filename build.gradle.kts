plugins {
    id("fabric-loom") version "1.15-SNAPSHOT"
}

group = "com.realseasons"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:1.21.11") // change to your version
    mappings("net.fabricmc:yarn:1.21.11+build.4")
    modImplementation("net.fabricmc:fabric-loader:0.18.4")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.141.3+1.21.11")


    annotationProcessor("org.spongepowered:mixin:0.8.5")
}

loom {
    mixin {
        defaultRefmapName.set("real-seasons.refmap.json")
    }
}