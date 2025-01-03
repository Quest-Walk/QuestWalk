package questwalk.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureComposeAndroid() {
    with(plugins) {
        apply("org.jetbrains.kotlin.plugin.compose")
//        apply("org.jetbrains.kotlin.compose")
    }

    androidExtension.apply {
        dependencies {
            implementation(platform(libs.androidx.compose.bom))
            androidTestImplementation(platform(libs.androidx.compose.bom))

            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.tooling.preview)

            debugImplementation(libs.androidx.compose.ui.tooling)
        }
    }

    extensions.getByType<ComposeCompilerGradlePluginExtension>().apply {
        enableStrongSkippingMode.set(true)
        includeSourceInformation.set(true)
    }
}