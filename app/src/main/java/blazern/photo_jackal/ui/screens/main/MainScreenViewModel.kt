package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor() : ViewModel() {
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
}
