import com.hapataka.questwalk.configureHiltAndroid
import com.hapataka.questwalk.configureJUnitAndroid
import com.hapataka.questwalk.configureKotlinAndroid

plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureHiltAndroid()
configureJUnitAndroid()