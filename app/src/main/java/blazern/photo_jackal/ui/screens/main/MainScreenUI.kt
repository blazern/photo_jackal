package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import android.text.format.Formatter
import android.util.Size
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImagePicker
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import kotlinx.coroutines.delay

@Composable
fun MainScreenUI(
    state: State<MainScreenState>,
    onCompressLevelChange: (Float)->Unit,
    onResolutionScaleChange: (Float)->Unit,
    onUserPickedImage: (Uri?)->Unit,
    onShareImageClick: ()->Unit,
    onRemoveImageClick: ()->Unit,
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
            horizontalAlignment = Alignment.End,
        ) {
            val selectedImage by remember { derivedStateOf { state.value.selectedImage } }
            val compressedImage by remember { derivedStateOf { state.value.compressedImage } }
            val imageProcessingStartTime by remember {
                derivedStateOf {
                    if (!state.value.processingImage) null else state.value.processingImageStartTime
                }
            }
            AnimatedVisibility(
                modifier = Modifier.Companion.weight(10f),
                visible = compressedImage != null,
                enter = slideInVertically(
                    // Start the slide from 40 (pixels) above where the content is supposed to go, to
                    // produce a parallax effect
                    initialOffsetY = { 40 },
                    animationSpec = keyframes {
                        this.durationMillis = 3000
                    }
                ),
//                        +
//                        expandVertically(expandFrom = Alignment.Top) +
//                        scaleIn(
//                            // Animate scale from 0f to 1f using the top center as the pivot point.
//                            transformOrigin = TransformOrigin(0.5f, 0f)
//                        ) +
//                        fadeIn(initialAlpha = 0.3f),
                exit = fadeOut(targetAlpha = 1f)
            ) {
                ImageContainer(
                    compressedImage,
                    imageProcessingStartTime,
                    onSettingsClick,
                    Modifier,
                )
            }
            if (selectedImage == null) {
                Box(modifier = Modifier
                    .weight(10f)
                    .fillMaxSize()) {
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.image_hint),
                            style = MaterialTheme.typography.labelLarge)
                    }
                    Image(
                        painterResource(R.drawable.big_arrow), "",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 100.dp, bottom = 32.dp)
                    )
                }
            }
            ShareRemoveButtons(state, onRemoveImageClick, onShareImageClick)
            val controlsAlpha by remember { derivedStateOf {
                if (state.value.selectedImage != null) 1f else 0f
            } }
            if (selectedImage != null) {
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
                        .padding(start = 16.dp, end = 16.dp),
                )
            } else {
                ImagePicker(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                ) {
                    onUserPickedImage.invoke(it)
                }
            }
        }
    }
}

@Composable
private fun ShareRemoveButtons(
    state: State<MainScreenState>,
    onRemoveImageClick: () -> Unit,
    onShareImageClick: () -> Unit
) {
    if (state.value.selectedImage != null) {
        Row(modifier = Modifier.padding(end = 16.dp)) {
            Button({ onRemoveImageClick.invoke() }) {
                Text(stringResource(R.string.clear_image))
            }
            ShareButton(
                state,
                onShareImageClick,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun ShareButton(
    state: State<MainScreenState>,
    onShareImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val compressedSize by remember { derivedStateOf { state.value.compressedImageSize } }
    val compressedImageUri by remember { derivedStateOf { state.value.compressedImage } }
    if (compressedSize != null && compressedImageUri != null) {
        val sizeHumanReadable =
            Formatter.formatShortFileSize(LocalContext.current, compressedSize?.toLong() ?: 0)
        Button(onClick = { onShareImageClick() }, modifier = modifier) {
            Text(stringResource(R.string.share) + " ($sizeHumanReadable)")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithImage() {
    val state = remember {
        mutableStateOf(
            MainScreenState(
                processingImage = false,
                selectedImage = Uri.parse("https://web.site/img.jpg"),
                selectedImageResolution = Size(1000, 1000),
                compressedImage = Uri.parse("https://web.site/img.jpg"),
                compressedImageResolution = Size(1000, 1000),
                compressedImageMinimalResolution = Size(10, 10),
                compressedImageSize = 123456,
                imageCompressLevel = 0.5f,
                imageResolutionScale = 0.5f,
            ),
        )
    }
    PhotoJackalTheme {
        Column {
            Button(onClick = {
                if (state.value.selectedImage == null) {
                    state.value = state.value.copy(
                        selectedImage = Uri.parse("https://web.site/img.jpg"),
                        compressedImage = Uri.parse("https://web.site/img.jpg"),
                    )
                } else {
                    state.value = state.value.copy(
                        selectedImage = null,
                        compressedImage = null,
                    )
                }
            }) {
                Text("reset")
            }
            MainScreenUI(
                state = state,
                onCompressLevelChange = {},
                onResolutionScaleChange = {},
                onSettingsClick = {},
                onRemoveImageClick = {},
                onShareImageClick = {},
                onUserPickedImage = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithoutImage() {
    val state = remember {
        mutableStateOf(
            MainScreenState(),
        )
    }
    PhotoJackalTheme {
        MainScreenUI(
            state = state,
            onCompressLevelChange = {},
            onResolutionScaleChange = {},
            onSettingsClick = {},
            onRemoveImageClick = {},
            onShareImageClick = {},
            onUserPickedImage = {},
        )
    }
}