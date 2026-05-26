plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
    id("replee.android.application")
    id("replee.android.hilt")
}

android {
    namespace = "com.nhuhuy.replee"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:data"))
    implementation(project(":core:test"))
    implementation(project(":core:design_system"))
    implementation(project(":core:sync"))
    implementation(project(":feature_home"))
    implementation(project(":feature_chat"))
    implementation(project(":feature_auth"))
    implementation(project(":feature_profile"))

    //Work Manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    //Paging for Ui
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    //Splash
    implementation(libs.androidx.core.splashscreen)

    //Nav3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)

    // Tests
    testImplementation(libs.robolectric)
}
