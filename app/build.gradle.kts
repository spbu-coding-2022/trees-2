/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("org.tree.kotlin-application-conventions")
    kotlin("plugin.serialization") version "1.8.20"
}

dependencies {
    implementation(project(":binaryTree"))

    implementation("org.neo4j.driver", "neo4j-java-driver", "5.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
}

application {
    // Define the main class for the application.
    mainClass.set("org.tree.app.AppKt")
}
