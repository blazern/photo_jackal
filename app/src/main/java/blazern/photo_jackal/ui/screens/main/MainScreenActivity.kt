package blazern.photo_jackal.ui.screens.main

import android.net.Uri
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
import androidx.compose.runtime.Composable
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
                MainScreenUI(
                    state = viewModel.state,
                    onCompressLevelChange = { viewModel.onCompressLevelChange(it) },
                    onResolutionScaleChange = { viewModel.onResolutionScaleChange(it) },
                    onUserPickedImage = ::onUserPickedImage,
                    onShareImageClick = ::shareCompressedImage,
                )
            }
        }
    }

    private fun onUserPickedImage(imageUri: Uri?) {
        if (imageUri != null) {
            cropImageLauncher.launch(
                CropImageContractOptions(
                    uri = imageUri,
                    cropImageOptions = CropImageOptions()
                )
            )
        } else {
            viewModel.onImagePicked(Result.success(null))
        }
    }

    private fun shareCompressedImage() {
        val compressedImageUri = viewModel.state.value.compressedImage
        compressedImageUri?.let { shareImage(it) }
    }
}

