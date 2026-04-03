plugins {
    id("replee.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.nhuhuy.replee.core.design_system"
}

dependencies {
    api(libs.coil.compose)
    api(libs.coil.network.okhttp)
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material.kolor)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}