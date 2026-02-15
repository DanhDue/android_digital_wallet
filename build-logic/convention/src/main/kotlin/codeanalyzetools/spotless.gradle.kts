package codeanalyzetools

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin

apply<SpotlessPlugin>()

configure<SpotlessExtension> {

    format("xml") {
        target("**/res/**/*.xml")
        indentWithSpaces(4)
        trimTrailingWhitespace()
    }

    java {
        target(
            fileTree(
                mapOf(
                    "dir" to "src",
                    "include" to listOf("**/*.java"),
                ),
            ),
        )
        licenseHeaderFile(
            file("$rootDir/config/copyright/copyright.kt"),
            "^(package|object|import|interface|@file:)",
        )
        googleJavaFormat().aosp()
        removeUnusedImports()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlin {
        target(
            fileTree(
                mapOf(
                    "dir" to ".",
                    "include" to listOf("**/*.kt"),
                    "exclude" to listOf("**/build/**", "**/buildSrc/**", "**/.*"),
                ),
            ),
        )
        licenseHeaderFile(
            file("$rootDir/config/copyright/copyright.kt"),
            "^(package|object|import|interface|@file:)",
        )
        trimTrailingWhitespace()
        indentWithSpaces()
        ktlint("1.1.1").setEditorConfigPath("$rootDir/config/ktlint/.editorconfig")
    }
}
