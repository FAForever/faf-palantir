import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    val kotlinVersion = "2.0.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    id("io.quarkus")
    id("com.diffplug.spotless") version "6.25.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        setUrl("https://jitpack.io")
    }
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-rest-qute")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-rest-client-oidc-filter")
    implementation("io.quarkus:quarkus-rest-client-oidc-token-propagation")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")
    implementation("com.github.jasminb:jsonapi-converter:0.13")
    implementation("com.github.FAForever.faf-java-commons:faf-commons-api:763f32222acf0011c6b8b36dac9e0462eb433745")
    implementation("com.github.FAForever.faf-java-commons:faf-commons-data:763f32222acf0011c6b8b36dac9e0462eb433745")
    implementation("com.github.rutledgepaulv:q-builders:1.6")
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    implementation(kotlin("stdlib-jdk8"))
}

group = "com.faforever"
version = "1.0.0-SNAPSHOT"

java {
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}

allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters.set(true)
    }
}

kotlin {
    jvmToolchain(21)
}

spotless {
    val ktlintVersion = "1.3.0"
    kotlin {
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint(ktlintVersion)
    }
}
