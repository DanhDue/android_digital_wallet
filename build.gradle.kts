import java.io.FileInputStream
import java.util.Properties

plugins {
    id(Deps.ANDROID_GRADLE_PLUGIN_ID) apply false
    id(Deps.KOTLIN_GRADLE_PLUGIN_ID) apply false
    id(Deps.ANDROID_COMPOSE_PLUGIN_ID) apply false
    id(Deps.GOOGLE_SERVICE_GRADLE_PLUGIN_ID) version Versions.GOOGLE_SERVICE apply false
    id(Deps.SONAR_CLOUD) version Versions.SONAR_CLOUD apply true
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