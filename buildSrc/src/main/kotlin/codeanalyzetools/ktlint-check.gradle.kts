package codeanalyzetools

import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    version.set("1.1.1")
    debug.set(true)
    verbose.set(true)
    android.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    enableExperimentalRules.set(true)
    ignoreFailures.set(false)
    additionalEditorconfig.set(
        mapOf(
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
			"ktlint_function_signature_body_expression_wrapping" to "multiline",
			"ktlint_code_style" to "ktlint_official",
			"ktlint_experimental" to "enabled",
			"ktlint_ignore_back_ticked_identifier" to "true",
			"ktlint_standard_annotation" to "disabled",
        )
    )
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
        include("**/kotlin/**")
    }
}
