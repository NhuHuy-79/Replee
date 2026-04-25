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
}