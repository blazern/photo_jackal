package blazern.photo_jackal.ui.screens.onboarding

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class OnboardingManager @Inject constructor(private val context: Application) {
    private val prefs = context.getSharedPreferences("ONBOARDING", Context.MODE_PRIVATE)

    fun isOnboardingPassed() = prefs.getBoolean(ONBOARDING_PASSED, false)
    fun onOnboardingPassed() {
        prefs.edit { putBoolean(ONBOARDING_PASSED, true) }
    }

    companion object {
        private const val ONBOARDING_PASSED = "ONBOARDING_PASSED"
    }
}