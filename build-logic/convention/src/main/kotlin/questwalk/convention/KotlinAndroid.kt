package questwalk.convention

import gradle.kotlin.dsl.accessors._4b055a01bae563bd2c86a468691a3401.coreLibraryDesugaring
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid() {
    pluginManager.apply("org.jetbrains.kotlin.android")

    androidExtension.apply {
        compileSdk = 35

        defaultConfig {
            minSdk = 26
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_18
            targetCompatibility = JavaVersion.VERSION_18
            isCoreLibraryDesugaringEnabled = true
        }

        buildTypes {
//            getByName("debug") {
//                isMinifyEnabled = false
//                proguardFiles(
//                    getDefaultProguardFile("proguard-android-optimize.txt"),
//                    "proguard-rules.pro"
//                )
//            }

            getByName("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
    }

    configureKotlin()

    dependencies {
        coreLibraryDesugaring(libs.android.desugarJdkLibs)
    }
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)

            val warningsAsErrors: String? by project

            allWarningsAsErrors.set(warningsAsErrors.toBoolean())
            freeCompilerArgs.set(
                freeCompilerArgs.get() + listOf("-opt-in=kotlin.RequiresOptIn")
            )
        }
    }
}