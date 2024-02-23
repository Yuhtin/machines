import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

version = "1.0.0"

bukkit {
    name = "machines"
    main = "com.yuhtin.quotes.machines.MachinesPlugin"
    version = "${project.version}"
    authors = listOf("Yuhtin")
    apiVersion = "1.13"
    depend = listOf("helper")
}


repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.lucko.me")
    maven("https://libraries.minecraft.net")
}

dependencies {
    compileOnly("me.lucko:helper:5.6.10")

    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    compileOnly(fileTree("/libs"))

    implementation("de.tr7zw:item-nbt-api-plugin:2.12.2")
    implementation("com.github.HenryFabio:inventory-api:main-SNAPSHOT")
    implementation("com.github.HenryFabio:sql-provider:9561f20fd2")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveFileName.set("${project.name}-${project.version}.jar")
    destinationDirectory.set(file(project.rootDir.parent.toString() + "/artifacts"))

    relocate("de.tr7zw.nbtapi", "com.yuhtin.quotes.machines.libs.nbtapi")
    relocate("com.henryfabio.minecraft.inventoryapi", "com.yuhtin.quotes.machines.libs.inventoryapi")
    relocate("com.henryfabio.sqlprovider", "com.yuhtin.quotes.machines.libs.sqlprovider")
    relocate("com.zaxxer.hikari", "com.yuhtin.quotes.machines.libs.hikari")

    println("Shadowing ${project.name} to ${destinationDirectory.get()}")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
}