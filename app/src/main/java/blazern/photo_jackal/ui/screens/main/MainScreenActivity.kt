package blazern.photo_jackal.ui.screens.main

import android.os.Bundle
import android.text.format.Formatter.formatShortFileSize
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImagePicker
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import blazern.photo_jackal.util.shareImage
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import dagger.hilt.android.AndroidEntryPoint

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
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column(
                            modifier = Modifier.weight(10f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                val compressedImageUri by remember { derivedStateOf { viewModel.state.value.compressedImage } }
                                if (compressedImageUri != null) {
                                    AsyncImage(
                                        model = compressedImageUri,
                                        modifier = Modifier.fillMaxSize(),
                                        contentDescription = "Selected image",
                                    )
                                }
                                val processing by remember { derivedStateOf { viewModel.state.value.processingImage } }
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
                        val compressLevel by remember { derivedStateOf { viewModel.state.value.imageCompressLevel } }
                        Slider(
                            modifier = Modifier.weight(1f),
                            value = compressLevel,
                            onValueChange = { viewModel.onCompressLevelChange(it) }
                        )
                        val compressedImageResolution by remember { derivedStateOf { viewModel.state.value.compressedImageResolution } }
                        Row {
                            Text(text = "Resolution ")
                            val compressedWidth = compressedImageResolution?.width
                            val compressedHeight = compressedImageResolution?.height
                            if (compressedWidth != null && compressedHeight != null) {
                                Text(text = "${compressedWidth}x$compressedHeight")
                            }
                        }
                        val imageResolution by remember { derivedStateOf { viewModel.state.value.selectedImageResolution } }
                        val imageMinResolution by remember { derivedStateOf { viewModel.state.value.compressedImageMinimalResolution } }
                        val imageWidth = imageResolution?.width
                        val imageHeight = imageResolution?.height
                        val minWidth = imageMinResolution?.width
                        val minHeight = imageMinResolution?.height
                        if (imageWidth != null && imageHeight != null && minWidth != null && minHeight != null) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                val maxResolutionStr = "${imageWidth}x${imageHeight}"
                                val minResolutionStr = "${minWidth}x${minHeight}"
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
                        val resolutionScale by remember { derivedStateOf { viewModel.state.value.imageResolutionScale } }
                        Slider(
                            modifier = Modifier.weight(1f),
                            value = resolutionScale,
                            onValueChange = { viewModel.onResolutionScaleChange(it) }
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
                        val compressedSize by remember { derivedStateOf { viewModel.state.value.compressedImageSize } }
                        val compressedImageUri by remember { derivedStateOf { viewModel.state.value.compressedImage } }
                        if (compressedSize != null && compressedImageUri != null) {
                            val sizeHumanReadable = formatShortFileSize(this@MainScreenActivity, compressedSize?.toLong() ?: 0)
                            Button(onClick = {
                                shareImage(compressedImageUri!!)
                            }) {
                                Text("Share ($sizeHumanReadable)")
                            }
                        }
                    }
                }
            }
        }
    }
}

