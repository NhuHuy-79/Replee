plugins {
    id("replee.android.library")
    id("replee.android.hilt")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.nhuhuy.replee.core.database"
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    api(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    api(libs.androidx.room.paging)
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.serialization.json)
}