package blazern.photo_jackal.ui.root

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.coroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import blazern.photo_jackal.intents.IntentDispatcher
import blazern.photo_jackal.intents.StartsForResultsManagerNotRetained
import blazern.photo_jackal.ui.screens.main.MainScreen
import blazern.photo_jackal.ui.screens.onboarding.OnboardingManager
import blazern.photo_jackal.ui.screens.onboarding.OnboardingScreen
import blazern.photo_jackal.ui.screens.privacy_policy.PrivacyPolicyScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : ComponentActivity() {
    @Inject
    lateinit var onboardingManager: OnboardingManager
    @Inject
    lateinit var intentDispatcher: IntentDispatcher

    @SuppressWarnings("unused")
    @Inject
    lateinit var startsForResultsManager: StartsForResultsManagerNotRetained

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycle.coroutineScope.launch {
            intentDispatcher.dispatch(intent)
        }

        val startScreen = if (onboardingManager.isOnboardingPassed()) {
            Screen.MAIN
        } else {
            Screen.ONBOARDING
        }

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = startScreen.route) {
                composable(Screen.ONBOARDING.route) {
                    OnboardingScreen(
                        finish = {
                            onboardingManager.onOnboardingPassed()
                            navController.navigate(Screen.MAIN.route) {
                                popUpTo(0)
                            }
                        },
                        openPrivacyPolicy = {
                            navController.navigate(Screen.PRIVACY.route)
                        },
                    )
                }
                composable(Screen.MAIN.route) {
                    MainScreen(openScreen = {
                        navController.navigate(it.route)
                    })
                }
                composable(Screen.PRIVACY.route) { PrivacyPolicyScreen() }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        lifecycle.coroutineScope.launch {
            intentDispatcher.dispatch(intent)
        }
    }
}

