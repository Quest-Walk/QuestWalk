package questwalk.convention

import gradle.kotlin.dsl.accessors._4b055a01bae563bd2c86a468691a3401.implementation
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configurationCoroutineAndroid() {
    configurationCoroutineKotlin()

    dependencies {
        implementation(libs.coroutines.android)
    }
}

internal fun Project.configurationCoroutineKotlin() {
    dependencies {
        implementation(libs.coroutines.core)
    }
}