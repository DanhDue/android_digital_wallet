package codeanalyzetools

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

apply<DetektPlugin>()

plugins {
    id("io.gitlab.arturbosch.detekt")
}

configure<DetektExtension> {
    autoCorrect = true
    toolVersion = "1.23.8"
    parallel = false
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom(
        "${project.rootDir}/buildSrc/src/main/kotlin/codeanalyzetools/config/detekt/detekt.yml"
    )
    baseline =
        file("${project.rootDir}/buildSrc/src/main/kotlin/codeanalyzetools/config/detekt/baseline.xml")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:1.23.8")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-ruleauthors:1.23.8")
    detektPlugins("io.nlopez.compose.rules:detekt:0.4.12")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt-report.html"))
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt-report.xml"))
    }
}

tasks.withType<Detekt>().configureEach {
    // Only verify existing source directories to avoid "path does not exist" errors
    setSource(project.files("src/main/kotlin", "src/main/java").filter { it.exists() })

    include("**/*.kt", "**/*.kts")
    exclude("**/build/**", ".*/resources/.*", ".*test.*,.*/resources/.*,.*/tmp/.*")

    jvmTarget = JavaVersion.VERSION_21.toString()
}