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

            val gitVersionProvider =
                project.tasks.register("GitVersionTask", GitVersionTask::class.java) {
                    it.gitVersionOutputFile.set(
                        project.layout.buildDirectory
                            .map { it.file("intermediates/GitVersionTask/output") })
                    it.outputs.upToDateWhen { false } // never use cache
                }

            val buildTime = LocalDateTime.now()

            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->
                // Update version code
                val versionCodeTask =
                    project.tasks.register(
                        variant.name + "ManifestVersionCodeTask",
                        ManifestVersionCodeTask::class.java)
                    {
                        it.buildTime.set(buildTime)
                        it.outputs.upToDateWhen { false } // never use cache
                    }
                variant.artifacts.use(versionCodeTask)
                    .wiredWithFiles(
                        ManifestVersionCodeTask::mergedManifest,
                        ManifestVersionCodeTask::updatedManifest
                    ).toTransform(SingleArtifact.MERGED_MANIFEST)

                // Update version name
                val versionNameTask =
                    project.tasks.register(
                        variant.name + "ManifestVersionNameTask",
                        ManifestVersionNameTask::class.java)
                    {
                        it.buildTime.set(buildTime)
                        it.gitVersionFile.set(gitVersionProvider.flatMap(GitVersionTask::gitVersionOutputFile))
                        it.outputs.upToDateWhen { false } // never use cache
                    }
                variant.artifacts.use(versionNameTask)
                    .wiredWithFiles(
                        ManifestVersionNameTask::mergedManifest,
                        ManifestVersionNameTask::updatedManifest
                    ).toTransform(SingleArtifact.MERGED_MANIFEST)
            }
        }
    }
}