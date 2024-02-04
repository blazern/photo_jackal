package blazern.photo_jackal.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.calculateFileSize(fileUri: Uri): Int? {
    return withContext(Dispatchers.IO) {
        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            inputStream.available()
        }
    }
}
