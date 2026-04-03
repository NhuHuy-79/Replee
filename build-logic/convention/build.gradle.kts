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
        register("androidHilt") {
            id = "replee.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeature") {
            id = "replee.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidLibrary") {
            id = "replee.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidDomain") {
            id = "replee.android.domain"
            implementationClass = "AndroidDomainConventionPlugin"
        }
    }
}