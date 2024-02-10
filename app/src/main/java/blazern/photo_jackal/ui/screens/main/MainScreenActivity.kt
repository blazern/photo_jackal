package blazern.photo_jackal.ui.screens.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import blazern.photo_jackal.ui.screens.onboarding.OnboardingManager
import blazern.photo_jackal.ui.screens.privacy_policy.PrivacyPolicyActivity
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import blazern.photo_jackal.util.shareImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainScreenActivity : ComponentActivity() {
    @Inject
    lateinit var onboardingManager: OnboardingManager

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
        if (onboardingManager.maybeStartOnboardingFrom(this)) {
            return
        }
        setContent {
            PhotoJackalTheme {
                MainScreenUI(
                    state = viewModel.state,
                    onCompressLevelChange = { viewModel.onCompressLevelChange(it) },
                    onResolutionScaleChange = { viewModel.onResolutionScaleChange(it) },
                    onUserPickedImage = ::onUserPickedImage,
                    onShareImageClick = ::shareCompressedImage,
                    onRemoveImageCLick = { viewModel.removeSelectedImage() },
                    onSettingsClick = {
                        startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                    }
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

