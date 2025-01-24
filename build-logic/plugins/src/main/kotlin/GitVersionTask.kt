import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.IOException

abstract class GitVersionTask: DefaultTask() {
    @get:OutputFile
    abstract val gitVersionOutputFile: RegularFileProperty

    @TaskAction
    fun taskAction() {
         val proc = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
         val error = proc.errorStream.readBytes().decodeToString()
         if (error.isNotBlank()) {
              throw IOException("git error : $error")
         }
         val gitVersion = proc.inputStream.readBytes().decodeToString().trim()
         gitVersionOutputFile.get().asFile.writeText(gitVersion)
    }
}
