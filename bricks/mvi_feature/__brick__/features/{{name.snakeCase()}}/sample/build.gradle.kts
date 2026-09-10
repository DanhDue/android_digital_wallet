plugins {
    id(Deps.COMMONS_ANDROID_SAMPLE)
}

dependencies {
    implementation(project(":features:{{name.snakeCase()}}"))
}
