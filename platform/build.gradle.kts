import commons.addLibDefaultConfig
import extensions.api

plugins {
    id(Deps.COMMONS_ANDROID_LIBRARY)
    id(Deps.COMMONS_DAGGER_HILT)
}

android {
    namespace = "com.danhdue.platform"

    addLibDefaultConfig()

    buildFeatures {
        // Required only for the `LocalEntryProviderInstallers` CompositionLocal.
        compose = true
    }
}

dependencies {
    // Cross-feature navigation primitives: NavKey, EntryProviderScope.
    api(Deps.Navigation.nav3Runtime)
    // `@Serializable` annotation used by the AppRoutes NavKeys.
    api(Deps.Navigation.nav3SerializationCore)
    // CompositionLocal support for the moved EntryProviderInstaller registry.
    api(platform(Deps.Compose.composeBOM))
    api(Deps.Compose.runtime)
}
