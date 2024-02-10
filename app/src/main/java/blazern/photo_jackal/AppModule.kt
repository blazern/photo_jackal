package blazern.photo_jackal

import android.app.Application
import blazern.photo_jackal.ui.screens.onboarding.OnboardingManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Singleton
    @Provides
    fun provideMySingletonClass(application: Application): OnboardingManager =
        OnboardingManager(application)
}
