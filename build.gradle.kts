import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
    application
}

plugins.withId("org.springframework.boot") {
    springBoot {
        mainClass.set("nl.avisi.labs.techlabelasticsearch.TechlabElasticsearchApplicationKt")
    }
}

val assertjVersion = "3.17.2"
val elasticsearchVersion = "7.13.2"
val jacksonVersion = "2.12.3"
val junitVersion = "5.7.0"
val kotlinLoggingVersion = "2.0.8"
val mockkVersion = "1.10.0"
val mongoVersion = "4.2.7"
val springBootVersion = "2.5.0"
val springmockkVersion = "2.0.3"

group = "nl.avisi.labs.techlabelasticsearch"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("org.elasticsearch:elasticsearch:$elasticsearchVersion")
    implementation("org.elasticsearch.client:elasticsearch-rest-client:$elasticsearchVersion")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:$elasticsearchVersion")
    implementation("org.elasticsearch.client:elasticsearch-rest-client-sniffer:$elasticsearchVersion")
    implementation("org.litote.kmongo:kmongo:$mongoVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("com.ninja-squad:springmockk:$springmockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")

}

application {
    mainClass.set("nl.avisi.labs.techlabelasticsearch.TechlabElasticsearchApplicationKt")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
