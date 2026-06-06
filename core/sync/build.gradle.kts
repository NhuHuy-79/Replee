plugins {
    id("replee.android.library")
    id("replee.android.hilt")
}

android {
    namespace = "com.nhuhuy.replee.core.sync"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))

    // WorkManager
    api(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    
    implementation(libs.timber)
}
