plugins {
    id("replee.android.feature")
    id("replee.android.hilt")
}

android {
    namespace = "com.nhuhuy.replee.feature_chat"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))

    // Worker
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Zoomable
    implementation(libs.zoomable)
}