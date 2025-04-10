package blazern.photo_jackal.intents

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class StartsForResultsManagerNotRetained @Inject constructor(
    activity: Activity,
    val retained: StartsForResultsManager
) {
    init {
        (activity as ComponentActivity).lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                retained.onActivityCreate(activity)
            }
        })
    }
}
