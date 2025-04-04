import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.time.LocalDateTime
import java.time.Month

class ManifestVersionNameTaskTest {
    private lateinit var gitVersionFile: File
    private lateinit var mergedManifestFile: File
    private lateinit var updatedManifestFile: File
    private lateinit var task: ManifestVersionNameTask

    @Before
    fun setup() {
        val project = ProjectBuilder.builder().build()
        task = project.tasks.create("testManifestVersionName", ManifestVersionNameTask::class.java)
    }

    @Test
    fun `taskAction should update manifest with new versionName`() {
        gitVersionFile = File.createTempFile("gitVersion", ".txt").apply {
            writeText("eee3d5c")
            deleteOnExit()
        }

        mergedManifestFile = File.createTempFile("AndroidManifest", ".xml").apply {
            writeText(
                """
                <manifest package="com.example">
                    <application android:label="Example" android:versionName="1.0"/>
                </manifest>
                """.trimIndent()
            )
            deleteOnExit()
        }

        updatedManifestFile = File.createTempFile("UpdatedManifest", ".xml").apply {
            deleteOnExit()
        }

        task.gitVersionFile.set(gitVersionFile)
        task.mergedManifest.set(mergedManifestFile)
        task.updatedManifest.set(updatedManifestFile)
        task.buildTime.set(LocalDateTime.of(2025, Month.FEBRUARY, 4, 16, 32))

        task.taskAction()

        val updatedText = updatedManifestFile.readText()
        println("Updated manifest:\n$updatedText")

        assertTrue(
            updatedText,
            updatedText.contains("android:versionName=\"2025-02-04-16:32 eee3d5c\""),
        )
    }
}
