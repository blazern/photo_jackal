package blazern.photo_jackal

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun Context.getImageResolution(imageUri: Uri): Size? {
    return withContext(Dispatchers.IO) {
        val options = BitmapFactory.Options().apply {
            // Ensures that the bitmap is not loaded in memory
            inJustDecodeBounds = true
        }
        contentResolver.openInputStream(imageUri).use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
            val width = options.outWidth
            val height = options.outHeight
            if (width != -1 && height != -1) Size(width, height) else null
        }
    }
}
