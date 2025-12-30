package extensions

import EnvConfigs
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties

fun BaseExtension.setSigningConfigs(project: Project) = signingConfigs {
    create(EnvConfigs.BuildConfigKey.releaseAndroid9SigningName) {
        val keystorePropertiesFile =
            project.rootProject.file(EnvConfigs.BuildConfigKey.releaseAndroid9SigningConfigFile)
        if (!keystorePropertiesFile.exists()) {
            System.err.println("📜 Missing android9 file for release signing")
        } else {
            val keystoreProperties = Properties().apply {
                load(FileInputStream(keystorePropertiesFile))
            }
            try {
                storeFile = project.rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            } catch (e: Exception) {
                System.err.println("📜 android9 file is malformed")
            }
        }
    }

    create(EnvConfigs.BuildConfigKey.releaseAndroid11SigningName) {
        val keystorePropertiesFile =
            project.rootProject.file(EnvConfigs.BuildConfigKey.releaseAndroid11SigningConfigFile)
        if (!keystorePropertiesFile.exists()) {
            System.err.println("📜 Missing android11 file for release signing")
        } else {
            val keystoreProperties = Properties().apply {
                load(FileInputStream(keystorePropertiesFile))
            }
            try {
                storeFile = project.rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            } catch (e: Exception) {
                System.err.println("📜 android11 file is malformed")
            }
        }
    }

    getByName(EnvConfigs.BuildConfigKey.debugSigningConfigName) {
        val keystorePropertiesFile =
            project.rootProject.file(EnvConfigs.BuildConfigKey.releaseAndroid9SigningConfigFile)
        if (!keystorePropertiesFile.exists()) {
            System.err.println("📜 Missing android9 file for release signing")
        } else {
            val keystoreProperties = Properties().apply {
                load(FileInputStream(keystorePropertiesFile))
            }
            try {
                storeFile = project.rootProject.file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            } catch (e: Exception) {
                System.err.println("📜 android9 file is malformed")
            }
        }
    }
}
