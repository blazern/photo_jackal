package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImageDebugPlaceholder
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun ImageContainer(
    state: State<MainScreenState>,
    onRemoveImageCLick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val compressedImageUri by remember { derivedStateOf { state.value.compressedImage } }
        var currImageUri by remember { mutableStateOf<Uri?>(null) }
        var prevImageUri by remember { mutableStateOf<Uri?>(null) }
        var justSwitched by remember { mutableStateOf(false) }
        if (currImageUri != compressedImageUri) {
            currImageUri = compressedImageUri
            justSwitched = true
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (currImageUri != null) {
                Box {
                    if (prevImageUri != null) {
                        ImageWith(prevImageUri, zIndex = if (justSwitched) 10f else 1f)
                    }
                    ImageWith(
                        currImageUri,
                        zIndex = if (justSwitched) 1f else 10f,
                        onSuccess = {
                            prevImageUri = it
                        },
                    )
                }
                Button(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp)
                        .shadow(5.dp, shape = RoundedCornerShape(32.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    onClick = {
                        onRemoveImageCLick.invoke()
                    }) {
                    Text(stringResource(R.string.clear_image), color = Color.Black)
                }
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                onClick = onSettingsClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }
        }
        ProgressIndicatingBackground(state)
    }
}

@Composable
private fun ProgressIndicatingBackground(state: State<MainScreenState>) {
    val processing by remember { derivedStateOf { state.value.processingImage } }
    var processingTooLong by remember { mutableStateOf(false) }
    LaunchedEffect(listOf(state.value.processingImageStartTime, processing)) {
        if (processing) {
            delay(1000)
            processingTooLong = true
        } else {
            processingTooLong = false
        }
    }

    AnimatedVisibility(
        visible = processing && processingTooLong,
        enter = fadeIn(animationSpec = tween(1000)),
        exit = fadeOut(animationSpec = tween(1000)),
    ) {
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

@Composable
private fun ImageWith(uri: Uri?, zIndex: Float, onSuccess: ((Uri)->Unit)? = null) {
    AsyncImage(
        model = uri,
        placeholder = ImageDebugPlaceholder(R.drawable.placeholder_flat),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(zIndex),
        contentDescription = stringResource(R.string.selected_image),
        onSuccess = { uri?.let { onSuccess?.invoke(it) } }
    )
}