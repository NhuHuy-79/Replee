plugins {
    id("replee.android.library")
    id("replee.android.hilt")
}

android {
    namespace = "com.nhuhuy.replee.core.common"
}

dependencies {
    api(libs.androidx.material.icons.extended)
    api(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}