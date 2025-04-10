package blazern.photo_jackal.intents

import android.net.Uri
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import blazern.photo_jackal.TestActivity
import blazern.photo_jackal.crop.CropImageContract
import com.canhub.cropper.CropImageView
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StartsForResultsManagerTest {
    private val activityController = Robolectric.buildActivity(TestActivity::class.java)
    private val startsForResultsManager = StartsForResultsManager()

    @Test
    fun `starts for result, returns result`() {
        activityController.create()

        val activity = spyk(activityController.get())
        // Immediately start and finish the startForResult
        immediatelyGiveResultFrom(activity)
        startsForResultsManager.onActivityCreate(activity)

        var result: CropImageView.CropResult? = null
        val launcher = startsForResultsManager.registerForActivityResult(CropImageContract) {
            result = it
        }
        Assert.assertNull(result)

        // Launch
        launcher.launch(CropImageContract.Options(Uri.EMPTY))

        // Ensure we got the result
        Assert.assertNotNull(result)
    }

    private fun immediatelyGiveResultFrom(activity: TestActivity) {
        val callbackSlot = slot<ActivityResultCallback<CropImageView.CropResult>>()
        every {
            activity.registerForActivityResult(CropImageContract, capture(callbackSlot))
        } answers {
            val mockLauncher = mockk<ActivityResultLauncher<CropImageContract.Options>>(relaxed = true)
            every {
                mockLauncher.launch(any())
            } answers {
                callbackSlot.captured.onActivityResult(mockk())
            }
            mockLauncher
        }
    }

    @Test
    fun `starts for result, returns result, when registered after onStart`() {
        activityController.create()

        val activity = spyk(activityController.get())
        immediatelyGiveResultFrom(activity)
        startsForResultsManager.onActivityCreate(activity)

        // Start!
        activityController.start()

        var result: CropImageView.CropResult? = null
        val launcher = startsForResultsManager.registerForActivityResult(CropImageContract) {
            result = it
        }
        Assert.assertNull(result)

        // Launch
        launcher.launch(CropImageContract.Options(Uri.EMPTY))

        // Ensure we got the result
        Assert.assertNotNull(result)
    }

    @Test
    fun `can unregister from result`() {
        activityController.create()

        val activity = spyk(activityController.get())
        immediatelyGiveResultFrom(activity)
        startsForResultsManager.onActivityCreate(activity)

        var result: CropImageView.CropResult? = null
        val callback = ActivityResultCallback<CropImageView.CropResult> {
            result = it
        }
        // Register
        val launcher = startsForResultsManager.registerForActivityResult(CropImageContract, callback)
        // Unregister!
        startsForResultsManager.unregisterForActivityResult(CropImageContract, callback)

        launcher.launch(CropImageContract.Options(Uri.EMPTY))

        // Ensure we have no result, because we've unregistered
        Assert.assertNull(result)
    }
}
