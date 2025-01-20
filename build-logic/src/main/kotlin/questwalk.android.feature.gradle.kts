import com.hapataka.questwalk.configurationCoroutineAndroid
import com.hapataka.questwalk.configureHiltAndroid
import com.hapataka.questwalk.libs

plugins {
    id("questwalk.android.library")
    id("questwalk.android.compose")
}

configureHiltAndroid()
configurationCoroutineAndroid()

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.lifecycle.viewModelCompose)
}