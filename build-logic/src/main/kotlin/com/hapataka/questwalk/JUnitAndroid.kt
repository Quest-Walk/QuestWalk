package com.hapataka.questwalk

import gradle.kotlin.dsl.accessors._2fb5859a04200edaf14b854c40b2e363.testImplementation
import gradle.kotlin.dsl.accessors._4b055a01bae563bd2c86a468691a3401.androidTestImplementation
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureJUnitAndroid() {
    dependencies {
        testImplementation(libs.junit4)
        androidTestImplementation(libs.androidx.test.ext)
        androidTestImplementation(libs.androidx.test.ext.junit)
    }
}