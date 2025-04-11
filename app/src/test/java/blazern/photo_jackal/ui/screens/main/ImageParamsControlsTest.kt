package blazern.photo_jackal.ui.screens.main

import android.util.Size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ImageParamsControlsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `can click the resolution plus button`() {
        var compressLevel = 0.5f
        var resolutionScaleLevel = 0.5f
        composeTestRule.setContent {
            ImageParamsControls(
                selectedImageResolution = Size(100, 100),
                compressedImageResolution = Size(50, 50),
                compressedImageMinimalResolution = Size(10, 10),
                imageCompressLevel = compressLevel,
                imageResolutionScale = resolutionScaleLevel,
                onCompressLevelChange = {
                    compressLevel = it
                },
                onResolutionScaleChange = {
                    resolutionScaleLevel = it
                },
                modifier = Modifier
            )
        }

        composeTestRule.apply {
            Assert.assertEquals(0.5f, resolutionScaleLevel, 0.0001f)
            onNodeWithTag("resolution_plus").performClick()
            Assert.assertEquals(0.5f + 0.01f, resolutionScaleLevel, 0.0001f)
        }
    }

    @Test
    fun `can click the resolution minus button`() {
        var compressLevel = 0.5f
        var resolutionScaleLevel = 0.5f
        composeTestRule.setContent {
            ImageParamsControls(
                selectedImageResolution = Size(100, 100),
                compressedImageResolution = Size(50, 50),
                compressedImageMinimalResolution = Size(10, 10),
                imageCompressLevel = compressLevel,
                imageResolutionScale = resolutionScaleLevel,
                onCompressLevelChange = {
                    compressLevel = it
                },
                onResolutionScaleChange = {
                    resolutionScaleLevel = it
                },
                modifier = Modifier
            )
        }

        composeTestRule.apply {
            Assert.assertEquals(0.5f, resolutionScaleLevel, 0.0001f)
            onNodeWithTag("resolution_minus").performClick()
            Assert.assertEquals(0.5f - 0.01f, resolutionScaleLevel, 0.0001f)
        }
    }

    @Test
    fun `can click the compress plus button`() {
        var compressLevel = 0.5f
        var resolutionScaleLevel = 0.5f
        composeTestRule.setContent {
            ImageParamsControls(
                selectedImageResolution = Size(100, 100),
                compressedImageResolution = Size(50, 50),
                compressedImageMinimalResolution = Size(10, 10),
                imageCompressLevel = compressLevel,
                imageResolutionScale = resolutionScaleLevel,
                onCompressLevelChange = {
                    compressLevel = it
                },
                onResolutionScaleChange = {
                    resolutionScaleLevel = it
                },
                modifier = Modifier
            )
        }

        composeTestRule.apply {
            Assert.assertEquals(0.5f, compressLevel, 0.0001f)
            onNodeWithTag("compress_plus").performClick()
            Assert.assertEquals(0.5f + 0.01f, compressLevel, 0.0001f)
        }
    }

    @Test
    fun `can click the compress minus button`() {
        var compressLevel = 0.5f
        var resolutionScaleLevel = 0.5f
        composeTestRule.setContent {
            ImageParamsControls(
                selectedImageResolution = Size(100, 100),
                compressedImageResolution = Size(50, 50),
                compressedImageMinimalResolution = Size(10, 10),
                imageCompressLevel = compressLevel,
                imageResolutionScale = resolutionScaleLevel,
                onCompressLevelChange = {
                    compressLevel = it
                },
                onResolutionScaleChange = {
                    resolutionScaleLevel = it
                },
                modifier = Modifier
            )
        }

        composeTestRule.apply {
            Assert.assertEquals(0.5f, compressLevel, 0.0001f)
            onNodeWithTag("compress_minus").performClick()
            Assert.assertEquals(0.5f - 0.01f, compressLevel, 0.0001f)
        }
    }
}