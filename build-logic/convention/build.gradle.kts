plugins {
    `kotlin-dsl`
}

group = "com.android.replee.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "replee.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
    }
}