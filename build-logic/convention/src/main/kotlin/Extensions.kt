import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import java.util.Properties

internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        compileSdk = ProjectConfig.COMPILE_SDK

        defaultConfig {
            minSdk = ProjectConfig.MIN_SDK
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
                excludes += "**/res/font/roboto*"
                excludes += "**/res/font/notosans*"
                excludes += "**/res/font/noto_sans*"
                excludes += "**/res/font/noto_emoji*"
                excludes += "**/res/font/emoji*"
                excludes += "**/res/font/Noto*"
                excludes += "**/assets/fonts/Roboto*"
                excludes += "**/assets/fonts/Noto*"
                excludes += "**/assets/fonts/emoji*"
            }
        }

        lint {
            abortOnError = false
            checkReleaseBuilds = false
            warningsAsErrors = true
            disable += listOf("TypographyFractions", "TypographyQuotes")
        }
    }

    dependencies {
        add("implementation", libs.findLibrary("androidx-core-ktx").get())
        add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
        add("implementation", libs.findLibrary("androidx-appcompat").get())
        add("implementation", libs.findLibrary("material").get())

        // Common Test
        add("testImplementation", libs.findLibrary("junit").get())
        add("testImplementation", libs.findLibrary("mockk").get())
        add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
        libs.findLibrary("truth").ifPresent { add("testImplementation", it.get()) }
        libs.findLibrary("turbine").ifPresent { add("testImplementation", it.get()) }

        // Common Android Test
        libs.findLibrary("androidx-junit").ifPresent { add("androidTestImplementation", it.get()) }
        libs.findLibrary("androidx-espresso-core")
            .ifPresent { add("androidTestImplementation", it.get()) }
    }
}

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        buildFeatures.compose = true
        buildFeatures.resValues = true
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))

        add("implementation", libs.findLibrary("androidx-ui").get())
        add("implementation", libs.findLibrary("androidx-ui-graphics").get())
        add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-material3").get())
        add("implementation", libs.findLibrary("androidx-activity-compose").get())
        
        add("debugImplementation", libs.findLibrary("androidx-ui-tooling").get())
        libs.findLibrary("androidx-ui-test-manifest")
            .ifPresent { add("debugImplementation", it.get()) }
        libs.findLibrary("androidx-ui-test-junit4")
            .ifPresent { add("androidTestImplementation", it.get()) }
    }
}

internal fun Project.configureApplicationBuildTypes(
    applicationExtension: ApplicationExtension
) {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    val rawWebClientId = System.getenv("GOOGLE_WEB_CLIENT_ID")
        ?: localProperties.getProperty("GOOGLE_WEB_CLIENT_ID")
        ?: "dummy_id_for_ci"

    val cleanWebClientId = rawWebClientId.toString().replace("\"", "")

    applicationExtension.apply {
        signingConfigs {
            create("release") {
                val path = localProperties.getProperty("KEYSTORE_PATH")
                if (path != null && rootProject.file(path).exists()) {
                    storeFile = rootProject.file(path)
                    storePassword = localProperties.getProperty("KEYSTORE_PASSWORD")
                    keyAlias = localProperties.getProperty("KEY_ALIAS")
                    keyPassword = localProperties.getProperty("KEY_PASSWORD")
                }
            }
        }

        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                isShrinkResources = true

                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("release")
                buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$cleanWebClientId\"")
            }

            getByName("debug") {
                buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$cleanWebClientId\"")
            }
        }
    }
}
