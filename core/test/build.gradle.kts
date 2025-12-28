plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

dependencies{
    api(libs.junit)
    api(libs.robolectric)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk)
    api(libs.truth)
}
