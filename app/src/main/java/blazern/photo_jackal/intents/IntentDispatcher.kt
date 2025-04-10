package blazern.photo_jackal.intents

import android.content.Intent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class IntentDispatcher @Inject constructor() {
    private val receivers = mutableListOf<(Intent) -> Boolean>()
    private var lastIntent: Intent? = null

    /**
     * Will immediately receive last unconsumed Intent, if present
     */
    fun addReceiver(receiver: (Intent) -> Boolean) {
        receivers.add(receiver)
        val lastIntent = lastIntent
        if (lastIntent != null) {
            val consumed = receiver.invoke(lastIntent)
            if (consumed) {
                this.lastIntent = null
            }
        }
    }

    fun removeReceiver(receiver: (Intent) -> Boolean) {
        receivers.remove(receiver)
    }

    fun dispatch(intent: Intent) {
        for (receiver in receivers) {
            val consumed = receiver.invoke(intent)
            if (consumed) {
                return
            }
        }
        lastIntent = intent
    }
}
