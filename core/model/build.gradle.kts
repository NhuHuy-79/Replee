plugins {
    id("replee.android.library")
}

android {
    namespace = "com.nhuhuy.replee.core.model"
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
