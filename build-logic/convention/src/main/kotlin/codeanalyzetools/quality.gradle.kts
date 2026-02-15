package codeanalyzetools

plugins {
    id("codeanalyzetools.detekt-check")
    id("codeanalyzetools.spotless")
}

tasks.getByName("check") {
    setDependsOn(
        listOf(
            tasks.getByName("spotlessApply"),
            tasks.getByName("spotlessCheck"),
            tasks.getByName("detekt"),
        )
    )
}

val codeAnalyze by tasks.registering {
    setDependsOn(
        listOf(
            tasks.getByName("detekt"),
            tasks.getByName("spotlessApply")
        )
    )
}
