plugins {
    id("replee.android.library")
    id("replee.android.hilt")
}

android {
    namespace = "com.nhuhuy.replee.core.data"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    api(libs.androidx.datastore.preferences)
    api(libs.flow.operators)
    api(libs.timber)
}