plugins {
    id("replee.android.feature")
    id("replee.android.hilt")
}

android {
    namespace = "com.nhuhuy.replee.feature_home"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:sync"))

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}