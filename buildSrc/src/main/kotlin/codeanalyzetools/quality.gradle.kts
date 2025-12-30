package codeanalyzetools

plugins {
    id("codeanalyzetools.detekt-check")
    id("codeanalyzetools.ktlint-check")
    id("codeanalyzetools.spotless")
}

tasks.getByName("check") {
    setDependsOn(
        listOf(
            tasks.getByName("ktlintFormat"),
            tasks.getByName("ktlintCheck"),
            tasks.getByName("detekt"),
            tasks.getByName("spotlessApply")
        )
    )
}

val codeAnalyze by tasks.registering {
    setDependsOn(
        listOf(
            tasks.getByName("ktlintFormat"),
            tasks.getByName("ktlintCheck"),
            tasks.getByName("detekt"),
            tasks.getByName("spotlessApply")
        )
    )
}
