
plugins {
    id("replee.android.library")
    id("replee.android.hilt")
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.nhuhuy.replee.core.network"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:domain"))
    api(libs.firebase.auth)
    api(libs.androidx.credentials)
    api(libs.androidx.credentials.play.services.auth)
    api(libs.googleid)
    api(libs.firebase.database)
    api(libs.firebase.firestore)
    api(libs.firebase.messaging)
    api(libs.kotlinx.serialization.json)
    //Retrofit
    api(libs.retrofit)
    api(libs.converter.gson)
    api(libs.logging.interceptor)
}