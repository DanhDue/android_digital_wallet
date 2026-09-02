import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * `:konsist-test` — the architecture-enforcement gate.
 *
 * A plain JVM / JUnit module: it is **not** an Android module and is never a
 * dependency of `:app`, so it can never ship in the APK. It only reads the
 * repository's Kotlin sources (via `Konsist.scopeFromProject()`) and asserts the
 * K1–K9 architecture rules from the epic design §6.1 as ordinary unit tests.
 *
 * Run with `./gradlew :konsist-test:test`.
 */
plugins {
    kotlin("jvm")
    id("codeanalyzetools.spotless")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    testImplementation(Deps.Test.konsist)
    testImplementation(Deps.Test.junit)
}

tasks.test {
    useJUnit()
    testLogging {
        events("passed", "skipped", "failed")
    }
    // Konsist reads the repository tree directly (not via Gradle inputs) and the
    // rules also parse two checked-in governance files. Gradle cannot see any of
    // that, so without this the gate would report UP-TO-DATE after a source or
    // whitelist change. The scan is a few seconds — always run it.
    inputs
        .files(
            rootProject.file("scripts/konsist_boundary_whitelist.txt"),
            layout.projectDirectory.file("konsist_baseline.txt"),
        ).withPropertyName("governanceFiles")
    outputs.upToDateWhen { false }
}
