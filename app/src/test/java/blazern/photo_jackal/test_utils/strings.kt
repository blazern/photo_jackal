package blazern.photo_jackal.test_utils

import androidx.annotation.StringRes
import org.robolectric.RuntimeEnvironment

fun getStr(@StringRes id: Int) = RuntimeEnvironment.getApplication().getString(id)
