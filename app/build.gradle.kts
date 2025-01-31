import com.android.manifmerger.ManifestSystemProperty
import org.gradle.api.internal.DocumentationRegistry.BASE_URL

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.abrak"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.abrak"
        minSdk = 28
        targetSdk = 34
        versionCode = 1000
        versionName = "1.0.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    viewBinding{
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.picasso)
    implementation(libs.retrofit)
    implementation(libs.gson.converter)
    implementation(libs.gson)
    implementation(libs.loggin.interceptor)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.play.services.location)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.rx.android)
    implementation(libs.rx.java)
    implementation(libs.rxjava.retrofit.adapter)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
//    implementation(libs.koin.bom)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}