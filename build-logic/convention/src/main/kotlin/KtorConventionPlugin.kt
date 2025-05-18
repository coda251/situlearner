import com.coda.situlearner.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class KtorConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                "implementation"(libs.findLibrary("ktor-client-cio").get())
                "implementation"(libs.findLibrary("ktor-client-content-negotiation").get())
                "api"(libs.findLibrary("ktor-client-core").get())
                "implementation"(libs.findLibrary("ktor-serialization-kotlinx-json").get())
            }
        }
    }
}