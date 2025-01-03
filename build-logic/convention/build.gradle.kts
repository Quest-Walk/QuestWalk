plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)

    // build-logic에서 versionCatalog에 접근할 수 있도록 해주는 LibrariesForLibs 클래스 생성 가능
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "questwalk.android.hilt"
            implementationClass = "com.questwalk.convention.HiltAndroidPlugin"
        }
    }
}