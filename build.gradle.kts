import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Properties

plugins {
    id(Deps.ANDROID_GRADLE_PLUGIN_ID) apply false
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID) apply false
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID) apply false
    id(Deps.GOOGLE_SERVICE_GRADLE_PLUGIN_ID) version Versions.GOOGLE_SERVICE apply false
    id(Deps.SONAR_CLOUD) version Versions.SONAR_CLOUD apply true
//    alias(libs.plugins.android.dynamic.feature) apply false
}

apply<codequality.DependencyUpdatePlugin>()

apply(plugin = "codeanalyzetools.jacoco-multi-report")

/**
 * load a property from the local property files.
 */
fun getLocalProperties(file: String = "local.properties"): Properties {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        java.io.InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File from not found, file name = $file")

    return properties
}

tasks.register("checkDuplicateAssets") {
    group = "verification"
    description = "Scan to detect duplicate assets."

    doLast {
        val filesMap = mutableMapOf<String, MutableList<String>>()
        val projectDir = project.projectDir

        // scan resources in all res folders.
        fileTree(projectDir).apply {
            include("**/res/drawable*/*.png", "**/res/drawable*/*.jpg", "**/res/drawable*/*.webp", "**/res/drawable*/*.xml")
        }.forEach { file ->
            if (file.isFile) {
                val bytes = file.readBytes()
                val md = MessageDigest.getInstance("MD5")
                val hash = md.digest(bytes).joinToString("") { "%02x".format(it) }

                if (!filesMap.containsKey(hash)) {
                    filesMap[hash] = mutableListOf()
                }
                filesMap[hash]?.add(file.absolutePath.replace(projectDir.absolutePath, ""))
            }
        }

        val duplicates = filesMap.filter { it.value.size > 1 }

        if (duplicates.isEmpty()) {
            println("✅ Great! No duplicate resources were found.")
        } else {
            println("⚠️ WARNING: detected duplicate resources:")
            duplicates.forEach { (hash, paths) ->
                println("\n[Hash: $hash]")
                paths.forEach { println("  - $it") }
            }
            println("\n👉 Tip: Review and move these files into the `core_resource` module (or equivalent) to optimize the app size.")
        }
    }
}
