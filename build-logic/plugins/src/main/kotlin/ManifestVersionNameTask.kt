import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

abstract class ManifestVersionNameTask: DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val gitVersionFile: RegularFileProperty

    @get:Input
    abstract val buildTime: Property<LocalDateTime>

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val mergedManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val buildTime = buildTime.get()
        val buildTimeStr = DateTimeFormatter
            .ofPattern("yyyy-MM-DD-HH:mm", Locale.ENGLISH)
            .format(buildTime)
        val gitVersion = gitVersionFile.get().asFile.readText()
        val versionName = "$buildTimeStr $gitVersion"


        var manifest = mergedManifest.asFile.get().readText()
        manifest = manifest.replace(
            Regex("android:versionName.*=.*\".*\""),
            "android:versionName=\"$versionName\""
        )
        updatedManifest.get().asFile.writeText(manifest)
    }
}