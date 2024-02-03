package blazern.photo_jackal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream

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

/**
 * @param result will be written to on IO. It **will not be closed**.
 */
suspend fun Context.compressImage(imageUri: Uri, compressionQuality: Float, finalSize: Size, result: OutputStream) {
    withContext(Dispatchers.IO) {
        try {
            compressImageImpl(imageUri, compressionQuality, finalSize, result)
        } catch (e: IOException) {
            // TODO: report
        }
    }
}

private fun Context.compressImageImpl(imageUri: Uri, compressionQuality: Float, finalSize: Size, result: OutputStream) {
    // Get the orientation
    val orientation = contentResolver.openInputStream(imageUri).use { inputStream ->
        val exifInterface = inputStream?.let { ExifInterface(it) }
        exifInterface?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    } ?: throw IOException("Could not get image orientation: $imageUri")

    // Get the bitmap from Uri
    val originalBitmap = contentResolver.openInputStream(imageUri).use { inputStream ->
        inputStream?.let { BitmapFactory.decodeStream(inputStream) }
    } ?: throw IOException("Could not get image bitmap: $imageUri")

    // Scale the bitmap
    var scaledBitmap = Bitmap.createScaledBitmap(
        originalBitmap, finalSize.width, finalSize.height, true)

    // Rotate
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    scaledBitmap = Bitmap.createBitmap(
        scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

    // Compress the bitmap
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, (compressionQuality*100).toInt(), result)
    result.flush()
}