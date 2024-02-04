package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter.formatShortFileSize
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImagePicker
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import blazern.photo_jackal.util.calculateFileSize
import blazern.photo_jackal.util.getImageResolution
import blazern.photo_jackal.util.shareImage
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainScreenActivity : ComponentActivity() {
    private val viewModel by viewModels<MainScreenViewModel>()

    private val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.onCropImageResult(Result.success(result.uriContent))
        } else {
            viewModel.onCropImageResult(Result.failure(result.error!!))
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
                                val minResolutionStr = "${(MIN_RESOLUTION_SCALE *width).toInt()}x${(MIN_RESOLUTION_SCALE *height).toInt()}"
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
                                cropImageLauncher.launch(
                                    CropImageContractOptions(uri = it, cropImageOptions = CropImageOptions())
                                )
                            } else {
                                viewModel.onImagePicked(Result.success(null))
                            }
                        }
                        if (compressedSize != null && compressedImageUri != null) {
                            Button(onClick = {
                                shareImage(compressedImageUri!!)
                            }) {
                                Text("Share ($compressedSize)")
                            }
                        }
                        // To force the `LaunchedEffect` to update each time URI updates
                        val uri by remember { derivedStateOf { viewModel.state.value.selectedImage } }
                        LaunchedEffect(compressLevel, resolutionScale, uri, imageResolution) {
                            lifecycleScope.launch {
                                uri?.let {
                                    try {
                                        processing = true
                                        imageResolution = getImageResolution(it)
                                        // Mapping from [0 .. 1] to [0.1 .. 1]
                                        val mappedScale = (resolutionScale * 1f- MIN_RESOLUTION_SCALE) + MIN_RESOLUTION_SCALE
                                        compressedImageUri = viewModel.compressAndSaveImage(
                                            it,
                                            compressionQuality = compressLevel,
                                            Size((imageResolution!!.width * mappedScale).toInt(), (imageResolution!!.height * mappedScale).toInt()),
                                        )
                                        val fileSize =calculateFileSize(compressedImageUri!!)
                                        compressedSize = formatShortFileSize(this@MainScreenActivity, fileSize?.toLong() ?: -1L)
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

    companion object {
        const val MIN_RESOLUTION_SCALE = 0.03f
    }
}

