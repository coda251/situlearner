import com.coda.situlearner.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                val bom = libs.findLibrary("koin-bom").get()
                "implementation"(platform(bom))
                "implementation"(libs.findLibrary("koin-core").get())
            }
        }
    }
}