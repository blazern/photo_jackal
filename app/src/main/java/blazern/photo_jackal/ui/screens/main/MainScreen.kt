package blazern.photo_jackal.ui.screens.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import blazern.photo_jackal.ui.root.Screen
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import blazern.photo_jackal.util.shareImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Composable
fun MainScreen(
    openScreen: (Screen)->Unit,
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val cropImageLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.onCropImageResult(Result.success(result.uriContent))
        } else {
            viewModel.onCropImageResult(Result.failure(result.error!!))
        }
    }

    val context = LocalContext.current

    PhotoJackalTheme {
        MainScreenUI(
            state = viewModel.state,
            onCompressLevelChange = { viewModel.onCompressLevelChange(it) },
            onResolutionScaleChange = { viewModel.onResolutionScaleChange(it) },
            onUserPickedImage = { imageUri ->
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
            },
            onRemoveImageCLick = { viewModel.removeSelectedImage() },
            onShareImageClick = {
                val compressedImageUri = viewModel.state.value.compressedImage
                compressedImageUri?.let { context.shareImage(it) }
            },
            onSettingsClick = {
                openScreen(Screen.PRIVACY)
            },
        )
    }
}
