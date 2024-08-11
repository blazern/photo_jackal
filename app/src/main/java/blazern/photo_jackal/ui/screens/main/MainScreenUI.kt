package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import android.text.format.Formatter
import android.util.Size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImagePicker
import blazern.photo_jackal.ui.theme.PhotoJackalTheme

@Composable
fun MainScreenUI(
    state: State<MainScreenState>,
    onCompressLevelChange: (Float)->Unit,
    onResolutionScaleChange: (Float)->Unit,
    onUserPickedImage: (Uri?)->Unit,
    onShareImageClick: ()->Unit,
    onRemoveImageCLick: ()->Unit,
    onSettingsClick: ()->Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageContainer(
                state,
                onRemoveImageCLick,
                onSettingsClick,
                Modifier.Companion.weight(10f),
            )
            val controlsAlpha by remember { derivedStateOf {
                if (state.value.selectedImage != null) 1f else 0f
            } }
            ImageParamsControls(
                imageCompressLevel = state.value.imageCompressLevel,
                compressedImageResolution = state.value.compressedImageResolution,
                selectedImageResolution = state.value.selectedImageResolution,
                compressedImageMinimalResolution = state.value.compressedImageMinimalResolution,
                imageResolutionScale = state.value.imageResolutionScale,
                onCompressLevelChange = onCompressLevelChange,
                onResolutionScaleChange = onResolutionScaleChange,
                modifier = Modifier
                    .weight(4f)
                    .alpha(controlsAlpha)
                    .wrapContentHeight()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            )
            val selectedImage by remember { derivedStateOf { state.value.selectedImage } }
            if (selectedImage == null) {
                ImagePicker(modifier = Modifier.weight(1f)) {
                    onUserPickedImage.invoke(it)
                }
            }
            val compressedSize by remember { derivedStateOf { state.value.compressedImageSize } }
            val compressedImageUri by remember { derivedStateOf { state.value.compressedImage } }
            if (compressedSize != null && compressedImageUri != null) {
                val sizeHumanReadable =
                    Formatter.formatShortFileSize(LocalContext.current, compressedSize?.toLong() ?: 0)
                Button(onClick = {
                    onShareImageClick()
                }) {
                    Text(stringResource(R.string.share) + " ($sizeHumanReadable)")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    val state = remember {
        mutableStateOf(
            MainScreenState(
                processingImage = false,
                selectedImage = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Sheikh_Hasina_in_the_Maldives_2021.jpg/122px-Sheikh_Hasina_in_the_Maldives_2021.jpg"),
                selectedImageResolution = Size(1000, 1000),
                compressedImage = Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/8/81/Sheikh_Hasina_in_the_Maldives_2021.jpg/122px-Sheikh_Hasina_in_the_Maldives_2021.jpg"),
                compressedImageResolution = Size(1000, 1000),
                compressedImageMinimalResolution = Size(10, 10),
                compressedImageSize = 123456,
                imageCompressLevel = 0.5f,
                imageResolutionScale = 0.5f,
            ),
        )
    }
    PhotoJackalTheme {
        MainScreenUI(
            state = state,
            onCompressLevelChange = {},
            onResolutionScaleChange = {},
            onSettingsClick = {},
            onRemoveImageCLick = {},
            onShareImageClick = {},
            onUserPickedImage = {},
        )
    }
}