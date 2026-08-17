import com.hapataka.questwalk.setNamespace

plugins {
    id("questwalk.android.library")
    id("questwalk.android.hilt")
}

setNamespace("core.remote")

dependencies {
    implementation(projects.core.dataApi)
    implementation(projects.core.domain)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.kotlinx.serialization.json)

    // ML Kit
    implementation(libs.mlkit.text.recognition.korean)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // Proj4j (좌표 변환)
    implementation(libs.proj4j)

    // Location
    implementation(libs.play.services.location)
    implementation(libs.coroutines.play.services)
}