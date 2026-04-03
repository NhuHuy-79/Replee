plugins {
    id("replee.android.domain")
}

android {
    namespace = "com.nhuhuy.replee.domain"
}
dependencies {
    implementation(libs.kotlinx.coroutines.core)
}