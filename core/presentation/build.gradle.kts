plugins {
    id("replee.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.nhuhuy.replee.core.presentation"
}

dependencies {
    api(project(":core:design_system"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)

    // Coil for Image Launchers/etc if needed
    implementation(libs.coil.compose)
}
