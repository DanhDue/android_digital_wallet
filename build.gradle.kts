import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Properties


plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.sonarqube) apply true
    alias(libs.plugins.versions) apply true
    alias(libs.plugins.codeanalyzetools.jacoco.multi.report) apply true
}

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
