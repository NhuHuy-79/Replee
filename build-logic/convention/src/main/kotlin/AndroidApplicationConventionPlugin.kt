import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)
                configureApplicationBuildTypes(this)
                defaultConfig {
                    applicationId = ProjectConfig.APP_ID
                    targetSdk = ProjectConfig.TARGET_SDK
                    versionCode = ProjectConfig.VERSION_CODE
                    versionName = ProjectConfig.VERSION_NAME
                    testInstrumentationRunner = ProjectConfig.TEST_INSTRUMENT
                }
            }
        }
    }
}