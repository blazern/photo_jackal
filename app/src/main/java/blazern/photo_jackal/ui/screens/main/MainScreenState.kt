package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import android.util.Size

data class MainScreenState(
    val processingImage: Boolean = false,

    val selectedImage: Uri? = null,
    val selectedImageResolution: Size? = null,

    val compressedImage: Uri? = null,
    val compressedImageResolution: Size? = null,
    val compressedImageMinimalResolution: Size? = null,
    val compressedImageSize: Int? = null,

    val imageCompressLevel: Float = 0.5f,
    val imageResolutionScale: Float = 0.5f,
)
