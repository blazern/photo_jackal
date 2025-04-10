package blazern.photo_jackal.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import blazern.photo_jackal.ui.root.Screen
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import blazern.photo_jackal.util.shareImage

@Composable
fun MainScreen(
    openScreen: (Screen)->Unit,
    viewModel: MainScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    PhotoJackalTheme {
        MainScreenUI(
            state = viewModel.state,
            onCompressLevelChange = { viewModel.onCompressLevelChange(it) },
            onResolutionScaleChange = { viewModel.onResolutionScaleChange(it) },
            onUserPickedImage = { imageUri ->
                if (imageUri != null) {
                    viewModel.cropImage(imageUri)
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
