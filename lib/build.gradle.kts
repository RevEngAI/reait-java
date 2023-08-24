plugins {
    `java-library`
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // toml settings file
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    
    // json for web requests
    implementation("org.json:json:20230618")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

// Read the environment variable
val apiKey: String = System.getenv("REAI_API_KEY") ?: "l1br3"

tasks.withType<Copy>().named("processTestResources") {
    filesMatching("**/reai-config.toml") {
        expand(mapOf("API_KEY_PLACEHOLDER" to apiKey))
    }
}
