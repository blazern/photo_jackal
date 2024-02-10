package blazern.photo_jackal.ui.screens.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.screens.privacy_policy.PrivacyPolicyActivity
import com.github.appintro.AppIntro
import com.github.appintro.AppIntroCustomLayoutFragment
import com.github.appintro.AppIntroFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardingActivity : AppIntro() {
    @Inject
    lateinit var onboardingManager: OnboardingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val privacyPolicyFragment = AppIntroCustomLayoutFragment.newInstance(R.layout.onboarding_privacy_policty)
        privacyPolicyFragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                privacyPolicyFragment.view?.findViewById<Button>(R.id.button_privacy_policy)?.setOnClickListener {
                    startActivity(Intent(this@OnboardingActivity, PrivacyPolicyActivity::class.java))
                }
            }
        })
        addSlide(privacyPolicyFragment)
        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.onboarding2_title),
            description = getString(R.string.onboarding2_descr),
            imageDrawable = R.drawable.jackal_pixelized,
        ))
        addSlide(AppIntroFragment.createInstance(
            title = getString(R.string.onboarding3_title),
            description = getString(R.string.onboarding3_descr),
            imageDrawable = R.drawable.jackal_laptop,
        ))
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        onboardingManager.onOnboardingPassedIn(this)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        onboardingManager.onOnboardingPassedIn(this)
    }
}