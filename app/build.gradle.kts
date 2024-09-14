import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

val properties = Properties()
properties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.project.tothestarlight"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.tothestarlight"
        minSdk = 32
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "${properties["api.key"]}")
        buildConfigField("String", "API_MOON", "${properties["url.moon"]}")
        buildConfigField("String", "API_ASTRO", "${properties["url.astro"]}")
        buildConfigField("String", "API_RISE", "${properties["url.rise"]}")
        buildConfigField("String", "API_WEATHER", "${properties["url.weather"]}")
        buildConfigField("String", "API_SHRTWEATHER", "${properties["url.shrtweather"]}")
        buildConfigField("String", "API_VERYSHRTWEATHER", "${properties["url.veryshrtweather"]}")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.material.calendar.view)
    implementation(libs.androidx.cardview)
    implementation(libs.lottie)
    implementation(libs.koreanlunarcalendar)
    implementation(libs.day.night.switch)
    implementation(libs.android.snowfall)
    implementation(libs.glide)
    annotationProcessor(libs.compiler)
}