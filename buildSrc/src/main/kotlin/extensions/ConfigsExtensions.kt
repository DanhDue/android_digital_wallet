package extensions

import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.ProductFlavor

fun ProductFlavor.buildStringConfigField(name: String, value: String) {
    this.buildConfigField("String", name, "\"$value\"")
}

fun ProductFlavor.buildBooleanConfigField(name: String, value: Boolean) {
    this.buildConfigField("boolean", name, "$value")
}

fun DefaultConfig.buildStringConfigField(name: String, value: String) {
    this.buildConfigField("String", name, "\"$value\"")
}

fun DefaultConfig.buildBooleanConfigField(name: String, value: Boolean) {
    this.buildConfigField("boolean", name, "$value")
}

