package blazern.photo_jackal.ui.screens.onboarding

import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import blazern.photo_jackal.R
import blazern.photo_jackal.test_utils.getStr
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingPageTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `normal case`() {
        composeTestRule.setContent {
            OnboardingPage(
                title = stringResource(R.string.onboarding2_title),
                R.drawable.jackal_pixelized,
                description = stringResource(R.string.onboarding2_descr),
            )
        }
        composeTestRule.onNodeWithText(getStr(R.string.onboarding2_title)).assertExists()
        composeTestRule.onNodeWithText(getStr(R.string.onboarding2_descr)).assertExists()
    }

    @Test
    fun `bottom content`() {
        composeTestRule.setContent {
            OnboardingPage(
                title = stringResource(R.string.onboarding2_title),
                R.drawable.jackal_pixelized,
                description = stringResource(R.string.onboarding2_descr),
            ) {
                Text("this is bottom content")
            }
        }
        composeTestRule.onNodeWithText("this is bottom content").assertExists()
    }
}
