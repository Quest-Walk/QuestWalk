
import questwalk.convention.configureHiltAndroid

plugins {
    id("questwalk.android.library")
    id("questwalk.android.compose")
}

//android {
//    packaging {
//        resources {
//            excludes.add("META-INF/**")
//        }
//    }
//    defaultConfig {
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//}

configureHiltAndroid()

dependencies {
    implementation(project(":core:designsystem"))
}