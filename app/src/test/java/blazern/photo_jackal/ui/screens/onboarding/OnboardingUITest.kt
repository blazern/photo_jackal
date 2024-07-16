package blazern.photo_jackal.ui.screens.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import blazern.photo_jackal.R
import blazern.photo_jackal.test_utils.getStr
import blazern.photo_jackal.test_utils.swipeRight
import blazern.photo_jackal.test_utils.swipeLeft
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `can swipe until the end and back`() {
        finishAndBackTest(
            moveForward = {
                composeTestRule.onNodeWithTag("onboarding_pages").swipeLeft()
            },
            moveBackward = {
                composeTestRule.onNodeWithTag("onboarding_pages").swipeRight()
            }
        )
    }

    private fun finishAndBackTest(
        moveForward: () -> Unit,
        moveBackward: () -> Unit,
    ) {
        var finishClicked = false
        val finish = {
            finishClicked = true
        }
        composeTestRule.setContent {
            OnboardingUI(
                finish = finish,
                openPrivacyPolicy = {},
            )
        }
        composeTestRule.apply {
            // First page
            onNodeWithText(getStr(R.string.onboarding1_descr)).assertIsDisplayed()
            // No finish button
            onNodeWithText(getStr(R.string.onboarding_finish)).assertIsNotDisplayed()

            var moves = 0
            while (onNodeWithText(getStr(R.string.onboarding_finish)).isNotDisplayed()) {
                moveForward()
                moves += 1
                assertTrue(moves < 10)
            }

            // Not first page anymore
            onNodeWithText(getStr(R.string.onboarding1_descr)).assertIsNotDisplayed()
            // Finish button visible
            onNodeWithText(getStr(R.string.onboarding_finish)).assertIsDisplayed()

            // Finish works
            assertFalse(finishClicked)
            onNodeWithText(getStr(R.string.onboarding_finish)).performClick()
            assertTrue(finishClicked)

            // Let's swipe back
            moves = 0
            while (onNodeWithText(getStr(R.string.onboarding1_descr)).isNotDisplayed()) {
                moveBackward()
                moves += 1
                assertTrue(moves < 10)
            }
            onNodeWithText(getStr(R.string.onboarding1_descr)).assertIsDisplayed()
            onNodeWithText(getStr(R.string.onboarding_finish)).assertIsNotDisplayed()
        }
    }

    @Test
    fun `can click the next button until the end and back`() {
        finishAndBackTest(
            moveForward = {
                composeTestRule.onNodeWithText(getStr(R.string.onboarding_next)).performClick()
            },
            moveBackward = {
                composeTestRule.onNodeWithText(getStr(R.string.onboarding_back)).performClick()
            }
        )
    }

    @Test
    fun `can click privacy policy`() {
        var privacyPolicyClicked = false
        val openPrivacyPolicy = {
            privacyPolicyClicked = true
        }
        composeTestRule.setContent {
            OnboardingUI(
                finish = {},
                openPrivacyPolicy = openPrivacyPolicy,
            )
        }
        composeTestRule.apply {
            var moves = 0
            while (onNodeWithText(getStr(R.string.onboarding1_privacy_policy_button)).isNotDisplayed()) {
                onNodeWithText(getStr(R.string.onboarding_next)).performClick()
                moves += 1
                assertTrue(moves < 10)
            }

            assertFalse(privacyPolicyClicked)
            onNodeWithText(getStr(R.string.onboarding1_privacy_policy_button)).performClick()
            assertTrue(privacyPolicyClicked)
        }
    }
}
