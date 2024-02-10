package blazern.photo_jackal.ui.screens.onboarding

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import blazern.photo_jackal.ui.screens.main.MainScreenActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingManager @Inject constructor(private val context: Application) {
    private val prefs = context.getSharedPreferences("ONBOARDING", Context.MODE_PRIVATE)

    fun maybeStartOnboardingFrom(activity: Activity): Boolean {
        if (prefs.getBoolean(ONBOARDING_PASSED, false)) {
            return false
        }
        activity.startActivity(Intent(activity, OnboardingActivity::class.java))
        activity.finish()
        return true
    }

    fun onOnboardingPassedIn(activity: Activity) {
        prefs.edit { putBoolean(ONBOARDING_PASSED, true) }
        activity.startActivity(Intent(activity, MainScreenActivity::class.java))
        activity.finish()
    }

    companion object {
        private const val ONBOARDING_PASSED = "ONBOARDING_PASSED"
    }
}