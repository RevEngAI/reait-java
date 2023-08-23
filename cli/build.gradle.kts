plugins {
    application
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClass.set("ai.reveng.toolkit.Cli")
}