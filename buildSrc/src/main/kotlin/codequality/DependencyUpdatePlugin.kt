package codequality

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.support.serviceOf
import org.gradle.process.ExecOperations
import java.util.*

class DependencyUpdatePlugin : Plugin<Project> {
    override fun apply(project: Project) =
        with(project) {
            plugins.apply("com.github.ben-manes.versions")
            tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
                rejectVersionIf {
                    isNonStable(candidate.version)
                }
                outputFormatter = "html"
                doLast {
                    serviceOf<ExecOperations>().exec {
                        commandLine("open", "${layout.buildDirectory.get()}/dependencyUpdates/report.html")
                    }
                }
            }
        }

    private fun isNonStable(version: String): Boolean {
        val stableKeyword =
            listOf("RELEASE", "FINAL", "GA").any {
                version.uppercase(Locale.getDefault())
                    .contains(it)
            }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        val isStable = stableKeyword || regex.matches(version)
        return isStable.not()
    }
}
