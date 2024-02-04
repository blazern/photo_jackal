package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import android.util.Size
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import blazern.photo_jackal.usecase.CompressAndSaveImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val compressAndSaveImageUseCase: CompressAndSaveImageUseCase,
) : ViewModel() {
    private val _state = mutableStateOf(MainScreenState(
        selectedImage = null,
    ))
    val state: State<MainScreenState> = _state

    fun onImagePicked(result: Result<Uri?>) {
        if (result.isSuccess) {
            _state.value = state.value.copy(selectedImage = result.getOrThrow())
        } else {
            throw result.exceptionOrNull()!!
        }
    }

    fun onCropImageResult(result: Result<Uri?>) {
        onImagePicked(result)
    }

    suspend fun compressAndSaveImage(imageUri: Uri, compressionQuality: Float, finalSize: Size) =
        compressAndSaveImageUseCase.execute(imageUri, compressionQuality, finalSize)
}
