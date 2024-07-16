package blazern.photo_jackal.ui.screens.onboarding

import androidx.compose.runtime.Composable

@Composable
fun OnboardingScreen(finish: () -> Unit, openPrivacyPolicy: () -> Unit) {
    OnboardingUI(finish, openPrivacyPolicy)
}
