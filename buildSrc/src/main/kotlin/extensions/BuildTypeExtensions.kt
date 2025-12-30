package extensions

import com.android.build.api.dsl.BuildType

fun BuildType.buildStringConfigField(name: String, value: String) {
    this.buildConfigField("String", name, "\"$value\"")
}

fun BuildType.buildBooleanConfigField(name: String, value: Boolean) {
    this.buildConfigField("boolean", name, "$value")
}
