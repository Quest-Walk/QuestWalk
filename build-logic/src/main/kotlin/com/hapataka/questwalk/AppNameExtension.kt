package com.hapataka.questwalk

import org.gradle.api.Project


fun Project.setNamespace(name: String) {
    androidExtension.apply {
        namespace = "com.hapataka.questwalk.$name"
    }
}