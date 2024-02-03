package blazern.photo_jackal

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter.formatShortFileSize
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class MainActivity : ComponentActivity() {
    private val imageUri = mutableStateOf<Uri?>(null)

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri.value = result.uriContent
        } else {
            throw result.error!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoJackalTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var imageResolution by remember { mutableStateOf<Size?>(null) }
                    var compressedImageUri by remember { mutableStateOf<Uri?>(null) }
                    var processing by remember { mutableStateOf(false) }
                    var compressedSize by remember { mutableStateOf<String?>(null) }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        var compressLevel by remember { mutableFloatStateOf(0.5f) }
                        var resolutionScale by remember { mutableFloatStateOf(1.0f) }
                        Column(
                            modifier = Modifier.weight(10f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (compressedImageUri != null) {
                                    AsyncImage(
                                        model = compressedImageUri,
                                        modifier = Modifier.fillMaxSize(),
                                        contentDescription = "Selected image",
                                    )
                                }
                                if (processing) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.5f))
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(50.dp),
                                        )
                                    }
                                }
                            }
                        }
                        Text(text = "Compression level")
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Image(painterResource(R.drawable.jackal_0), "", modifier = Modifier.weight(1f))
                            Image(painterResource(R.drawable.jackal_1), "", modifier = Modifier.weight(1f))
                            Image(painterResource(R.drawable.jackal_2), "", modifier = Modifier.weight(1f))
                            Image(painterResource(R.drawable.jackal_3), "", modifier = Modifier.weight(1f))
                            Image(painterResource(R.drawable.jackal_4), "", modifier = Modifier.weight(1f))
                            Image(painterResource(R.drawable.jackal_5), "", modifier = Modifier.weight(1f))
                        }
                        Slider(
                            modifier = Modifier.weight(1f),
                            value = compressLevel,
                            onValueChange = { compressLevel = it }
                        )
                        Row {
                            Text(text = "Resolution ")
                            if (imageResolution != null) {
                                val width = (imageResolution!!.width * resolutionScale).toInt()
                                val height = (imageResolution!!.height * resolutionScale).toInt()
                                Text(text = "${width}x${height}")
                            }
                        }
                        if (imageResolution != null) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                val width = imageResolution!!.width
                                val height = imageResolution!!.height
                                val maxResolutionStr = "${width}x${height}"
                                val minResolutionStr = "${(MIN_RESOLUTION_SCALE*width).toInt()}x${(MIN_RESOLUTION_SCALE*height).toInt()}"
                                Text(
                                    minResolutionStr,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    maxResolutionStr,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                        Slider(
                            modifier = Modifier.weight(1f),
                            value = resolutionScale,
                            onValueChange = { resolutionScale = it }
                        )
                        ImagePicker(modifier = Modifier.weight(2f)) {
                            if (it != null) {
                                cropImage.launch(
                                    CropImageContractOptions(uri = it, cropImageOptions = CropImageOptions())
                                )
                            } else {
                                imageUri.value = it
                            }
                        }
                        if (compressedSize != null && compressedImageUri != null) {
                            Button(onClick = {
                                shareImage(compressedImageUri!!)
                            }) {
                                Text("Share ($compressedSize)")
                            }
                        }
                        val uri by remember { imageUri } // To force the `LaunchedEffect` to update
                        LaunchedEffect(compressLevel, resolutionScale, uri, imageResolution) {
                            lifecycleScope.launch {
                                uri?.let {
                                    try {
                                        processing = true
                                        imageResolution = getImageResolution(it)
                                        compressedImageUri = compressImage(
                                            this@MainActivity,
                                            it,
                                            compressionQuality = compressLevel,
                                            // Mapping from [0 .. 1] to [0.1 .. 1]
                                            resolutionScale = (resolutionScale * 1f-MIN_RESOLUTION_SCALE) + MIN_RESOLUTION_SCALE,
                                        )
                                        val fileSize =calculateFileSize(compressedImageUri!!)
                                        compressedSize = formatShortFileSize(this@MainActivity, fileSize?.toLong() ?: -1L)
                                    } finally {
                                        processing = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun shareImage(imageUri: Uri) {
        // Convert file:// Uri to content:// Uri using FileProvider
        val contentUri: Uri = MyFileProvider.getUriForFile(
            this,
            File(imageUri.path!!)
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val chooser = Intent.createChooser(shareIntent, "Share Image")
        startActivity(chooser)
    }

    companion object {
        const val MIN_RESOLUTION_SCALE = 0.03f
    }
}

@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    onImagePicked: (Uri?)->Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onImagePicked.invoke(uri)
        }
    )

    var cameraImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onImagePicked.invoke(cameraImageUri)
            } else {
                onImagePicked.invoke(null)
            }
        }
    )

    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                imagePicker.launch("image/*")
            },
        ) {
            Text(text = "Select Image")
        }
        Button(
            onClick = {
                val uri = MyFileProvider.getImageUri(context)
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            },
        ) {
            Text(text = "Take photo")
        }
    }
}

private suspend fun compressImage(context: Context, imageUri: Uri, compressionQuality: Float, resolutionScale: Float): Uri? {
    return withContext(Dispatchers.IO) {
        // Get the orientation
        var inputStream = context.contentResolver.openInputStream(imageUri)
        val exifInterface = ExifInterface(inputStream!!)
        val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        inputStream.close()

        // Get the bitmap from Uri
        inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Calculate new dimensions
        val newWidth = (originalBitmap.width * resolutionScale).toInt()
        val newHeight = (originalBitmap.height * resolutionScale).toInt()

        // Scale the bitmap
        var scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

        // Rotate
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)

        // Compress the bitmap
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, (compressionQuality*100).toInt(), outputStream)
        val compressedData = outputStream.toByteArray()

        // Save the compressed bitmap to a file
        val compressedFile = createImageFile(context)
        val fileOutputStream = FileOutputStream(compressedFile)
        fileOutputStream.write(compressedData)
        fileOutputStream.flush()
        fileOutputStream.close()

        // Return the Uri of the compressed file
        Uri.fromFile(compressedFile)
    }
}

private fun createImageFile(context: Context): File {
    // Create an image file name (you might want to add a timestamp or a unique identifier)
    val fileName = "compressed_image"
    val storageDir = context.getExternalFilesDir(null)
    return File.createTempFile(
        fileName, /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}
