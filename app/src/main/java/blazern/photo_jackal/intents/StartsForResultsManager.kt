package blazern.photo_jackal.intents

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import blazern.photo_jackal.crop.CropImageContract
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

private typealias Contract = ActivityResultContract<*, *>

@ActivityRetainedScoped
class StartsForResultsManager @Inject constructor() {
    private val contractsResults = mutableMapOf<Contract, Any>()
    private val contractsCallbacks = mutableMapOf<Contract, ActivityResultCallback<Any>>()
    private val launchers = mutableMapOf<Contract, ActivityResultLauncher<*>>()

    internal fun onActivityCreate(activity: ComponentActivity) {
        registerContract(CropImageContract, activity)
    }

    private fun registerContract(
        contract: Contract,
        activity: ComponentActivity,
    ) {
        launchers[contract] = activity.registerForActivityResult(contract) { result ->
            contractsResults[contract] = result as Any
            contractsCallbacks[contract]?.onActivityResult(result)
        }
    }

    /**
     * Can be called at any time, even long after Activity.onStart().
     * The caller will received a saved result, if any.
     * @param callback - same as the callback passed to
     * [ComponentActivity.registerForActivityResult], but can be called immediately, even before
     * this function returns.
     */
    fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>,
    ): ActivityResultLauncher<I> {
        @Suppress("UNCHECKED_CAST")
        contractsCallbacks[contract] = callback as ActivityResultCallback<Any>

        contractsResults[contract]?.let { result ->
            contractsCallbacks[contract]?.onActivityResult(result)
        }

        @Suppress("UNCHECKED_CAST")
        return launchers[contract] as ActivityResultLauncher<I>
    }

    fun <I, O> unregisterForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>,
    ) {
        if (contractsCallbacks[contract] == callback) {
            contractsCallbacks.remove(contract)
            launchers.remove(contract)?.unregister()
        }
    }
}
