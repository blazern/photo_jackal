package blazern.photo_jackal.ui.screens.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.util.Size
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blazern.photo_jackal.crop.CropImageContract
import blazern.photo_jackal.intents.IntentDispatcher
import blazern.photo_jackal.intents.StartsForResultsManager
import blazern.photo_jackal.usecase.CalculateFileSizeUseCase
import blazern.photo_jackal.usecase.CompressAndSaveImageUseCase
import blazern.photo_jackal.usecase.GetImageResolutionUseCase
import com.canhub.cropper.CropImageView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val compressAndSaveImageUseCase: CompressAndSaveImageUseCase,
    private val getImageResolutionUseCase: GetImageResolutionUseCase,
    private val calculateFileSizeUseCase: CalculateFileSizeUseCase,
    private val intentDispatcher: IntentDispatcher,
    private val forResultsManager: StartsForResultsManager,
) : ViewModel() {
    private var recalculateAndSaveCompressedStateScheduled = false
    private val intentsReceiver: (Intent) -> Boolean
    private val activityResultCallback: ActivityResultCallback<CropImageView.CropResult>
    private val cropStarter: ActivityResultLauncher<CropImageContract.Options>

    private val _state = mutableStateOf(MainScreenState())
    val state: State<MainScreenState> = _state

    init {
        activityResultCallback =
            ActivityResultCallback { result ->
                if (result.isSuccessful) {
                    onCropImageResult(Result.success(result.uriContent))
                } else {
                    onCropImageResult(Result.failure(result.error!!))
                }
            }
        cropStarter = forResultsManager.registerForActivityResult(CropImageContract, activityResultCallback)

        intentsReceiver = this::onIntent
        intentDispatcher.addReceiver(intentsReceiver)
    }

    override fun onCleared() {
        super.onCleared()
        intentDispatcher.removeReceiver(intentsReceiver)
        forResultsManager.unregisterForActivityResult(CropImageContract, activityResultCallback)
    }

    private fun onIntent(intent: Intent): Boolean {
        val isImageSent = Intent.ACTION_SEND == intent.action
                && intent.type?.startsWith("image/") == true
        if (!isImageSent) {
            return false
        }
        val imageUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
        imageUri?.let {
            cropImage(it)
        }
        return true
    }

    fun cropImage(imageUri: Uri) {
        cropStarter.launch(
            CropImageContract.Options(imageUri)
        )
    }

    private fun onCropImageResult(result: Result<Uri?>) {
        onImagePicked(result)
    }

    fun removeSelectedImage() {
        onImagePicked(Result.success(null))
    }

    fun onImagePicked(result: Result<Uri?>) {
        if (!result.isSuccess) {
            throw result.exceptionOrNull()!!
        }

        _state.value = state.value.copy(selectedImage = result.getOrThrow())
        val selectedImage = result.getOrThrow()
        if (selectedImage != null) {
            viewModelScope.launch {
                val selectedImageResolution = getImageResolutionUseCase.execute(selectedImage)
                val compressedImageMinimalResolution = selectedImageResolution?.let {
                    Size(
                        (it.width * MIN_RESOLUTION_SCALE).roundToInt(),
                        (it.height * MIN_RESOLUTION_SCALE).roundToInt(),
                    )
                }
                _state.value = state.value.copy(
                    selectedImageResolution = selectedImageResolution,
                    compressedImageMinimalResolution = compressedImageMinimalResolution,
                )
                recalculateAndSaveCompressedState()
            }
        } else {
            _state.value = state.value.copy(
                selectedImage = null,
                selectedImageResolution = null,
                compressedImage = null,
                compressedImageResolution = null,
                compressedImageSize = null,
            )
        }
    }

    fun onCompressLevelChange(level: Float) {
        _state.value = state.value.copy(imageCompressLevel = level)
        viewModelScope.launch {
            recalculateAndSaveCompressedState()
        }
    }

    fun onResolutionScaleChange(scale: Float) {
        _state.value = state.value.copy(
            imageResolutionScale = scale,
        )
        viewModelScope.launch {
            recalculateAndSaveCompressedState()
        }
    }

    private suspend fun recalculateAndSaveCompressedState() {
        val selectedImage = state.value.selectedImage
        val selectedImageResolution = state.value.selectedImageResolution
        if (selectedImage == null || selectedImageResolution == null) {
            return
        }

        if (state.value.processingImage) {
            recalculateAndSaveCompressedStateScheduled = true
            return
        }

        _state.value = state.value.copy(
            processingImage = true,
            processingImageStartTime = SystemClock.uptimeMillis(),
        )
        try {
            // So that the resolution would never be 0x0
            val mappedResolutionScale = mapResolutionScale(state.value.imageResolutionScale)
            val compressedImageResolution = Size(
                (selectedImageResolution.width * mappedResolutionScale).roundToInt(),
                (selectedImageResolution.height * mappedResolutionScale).roundToInt()
            )
            val imageCompressLevel = state.value.imageCompressLevel
            val compressedImage = compressAndSaveImageUseCase.execute(
                selectedImage,
                imageCompressLevel,
                compressedImageResolution,
            )
            val compressedImageSize = calculateFileSizeUseCase.execute(compressedImage)
            _state.value = state.value.copy(
                compressedImage = compressedImage,
                compressedImageResolution = compressedImageResolution,
                compressedImageSize = compressedImageSize ?: -1,
            )
        } finally {
            _state.value = state.value.copy(processingImage = false)
            if (recalculateAndSaveCompressedStateScheduled) {
                recalculateAndSaveCompressedStateScheduled = false
                recalculateAndSaveCompressedState()
            }
        }
    }

    companion object {
        private const val MIN_RESOLUTION_SCALE = 0.03f
        private fun mapResolutionScale(scale: Float): Float =
            MIN_RESOLUTION_SCALE + scale * (1 - MIN_RESOLUTION_SCALE)
    }
}
