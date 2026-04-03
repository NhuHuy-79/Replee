plugins {
    id("replee.android.feature")
    id("replee.android.hilt")

}

android {
    namespace = "com.nhuhuy.replee.feature_auth"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    debugImplementation(libs.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}