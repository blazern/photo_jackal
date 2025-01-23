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

abstract class ManifestVersionCodeTask: DefaultTask() {
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
        // 2025 -> 25
        val year = buildTime.year.toString().substring(2)
        // 23 -> 023
        val day = buildTime.dayOfYear.toString().padStart(3, '0')
        // 12:29 -> 1229
        val hourMinute = DateTimeFormatter
            .ofPattern("HHmm", Locale.ENGLISH)
            .format(buildTime)

        // max versionCode = 2100000000
        // example result  = 250231229
        val buildTimeStr = "$year$day$hourMinute"

        var manifest = mergedManifest.asFile.get().readText()
        manifest = manifest.replace(
            Regex("android:versionCode.*=.*\"\\d+\""),
            "android:versionCode=\"$buildTimeStr\""
        )
        updatedManifest.get().asFile.writeText(manifest)
    }
}