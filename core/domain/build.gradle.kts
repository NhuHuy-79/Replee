plugins {
    id("replee.android.domain")
}

android {
    namespace = "com.nhuhuy.replee.core.domain"
}
dependencies {
    api(project(":core:model"))
    api(libs.androidx.paging.runtime)
    api(libs.androidx.work.runtime.ktx)
    implementation(libs.kotlinx.coroutines.core)
}