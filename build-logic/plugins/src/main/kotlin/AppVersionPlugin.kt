import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.time.LocalDateTime

class AppVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->
                val manifestUpdater =
                    project.tasks.register(
                        variant.name + "ManifestVersionCodeTask",
                        ManifestVersionCodeTask::class.java)
                    {
                        it.buildTime.set(LocalDateTime.now())
                        // never use cache
                        it.outputs.upToDateWhen { false }
                    }
                // update manifest
                variant.artifacts.use(manifestUpdater)
                    .wiredWithFiles(
                        ManifestVersionCodeTask::mergedManifest,
                        ManifestVersionCodeTask::updatedManifest
                    ).toTransform(SingleArtifact.MERGED_MANIFEST)
            }
        }
    }
}