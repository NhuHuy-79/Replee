
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"


}

android {
    namespace = "com.nhuhuy.replee.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    api(libs.firebase.auth)
    api(libs.androidx.credentials)
    api(libs.androidx.credentials.play.services.auth)
    api(libs.googleid)
    api(libs.firebase.firestore)
    api(libs.firebase.messaging)
    api(libs.kotlinx.serialization.json)
    api(libs.firebase.messaging)

    //CLOUDINARY
    /*api(libs.kotlin.url.gen)*/
    api(libs.cloudinary.android)

    //Hilt
    implementation(libs.dagger.hilt.android)
    testImplementation(libs.junit.junit)
    debugImplementation(libs.ui.tooling)
    ksp(libs.hilt.android.compiler)

    //KTOR
    api(libs.ktor.okhttp)
    api(libs.ktor.core)
    api(libs.ktor.json)
    api(libs.ktor.okhttp)
    api(libs.ktor.content.negotiation)
    api(libs.ktor.logging)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}