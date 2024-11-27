plugins {
    id("java")
    application
    kotlin("jvm") version "1.8.10"
}

group = "com.webcrawler"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // JSoup for HTML parsing
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("org.json:json:20210307")
    
    // SQLite for database
    implementation("org.xerial:sqlite-jdbc:3.41.2.2")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation(kotlin("stdlib"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

application {
    mainClass.set("com.webcrawler.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
