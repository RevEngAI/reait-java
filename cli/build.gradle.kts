plugins {
    application
}

dependencies {
    implementation(project(":lib"))
    implementation("commons-cli:commons-cli:1.5.0")
}

application {
    mainClass.set("ai.reveng.toolkit.CommandLineApp")
}