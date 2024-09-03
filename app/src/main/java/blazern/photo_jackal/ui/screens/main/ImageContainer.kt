package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImageDebugPlaceholder
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

@Composable
fun ImageContainer(
    imageUri: Uri?,
    imageProcessingStartTime: Long?,
    onSettingsClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        var currImageUri by remember { mutableStateOf<Uri?>(null) }
        var prevImageUri by remember { mutableStateOf<Uri?>(null) }
        var justSwitched by remember { mutableStateOf(false) }
        if (currImageUri != imageUri) {
            currImageUri = imageUri
            justSwitched = true
        }

        Box(modifier = Modifier.fillMaxSize()) {
//            AnimatedVisibility(
//                visible = currImageUri != null,
//                enter = slideInVertically(
//                    // Start the slide from 40 (pixels) above where the content is supposed to go, to
//                    // produce a parallax effect
//                    initialOffsetY = { 40 }
//                ),
////                        +
////                        expandVertically(expandFrom = Alignment.Top) +
////                        scaleIn(
////                            // Animate scale from 0f to 1f using the top center as the pivot point.
////                            transformOrigin = TransformOrigin(0.5f, 0f)
////                        ) +
////                        fadeIn(initialAlpha = 0.3f),
//                exit = fadeOut(targetAlpha = 1f)
//            ) {
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
//            }


//
//
//            val density = LocalDensity.current
//            AnimatedVisibility(
//                visible = currImageUri != null,
//                enter = slideInVertically {
//                    // Slide in from 40 dp from the top.
//                    with(density) { -40.dp.roundToPx() }
//                } + expandVertically(
//                    // Expand from the top.
//                    expandFrom = Alignment.Top
//                ) + fadeIn(
//                    // Fade in with the initial alpha of 0.3f.
//                    initialAlpha = 0.3f
//                ),
//                exit = slideOutVertically() + shrinkVertically() + fadeOut()
//            ) {
//                Box {
//                    if (prevImageUri != null) {
//                        ImageWith(prevImageUri, zIndex = if (justSwitched) 10f else 1f)
//                    }
//                    ImageWith(
//                        currImageUri,
//                        zIndex = if (justSwitched) 1f else 10f,
//                        onSuccess = {
//                            prevImageUri = it
//                        },
//                    )
//                }
//            }
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
        ProgressIndicatingBackground(imageProcessingStartTime)
    }
}

@Composable
private fun ProgressIndicatingBackground(imageProcessingStartTime: Long?) {
    val processing = imageProcessingStartTime != null
    var processingTooLong by remember { mutableStateOf(false) }
    LaunchedEffect(listOf(imageProcessingStartTime, processing)) {
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

@Preview(showBackground = true)
@Composable
private fun PreviewWithoutImage() {
    var imageShown by remember { mutableStateOf(true) }
    PhotoJackalTheme {
        ImageContainer(
            imageUri = if (imageShown) Uri.parse("https://web.site/img.jpg") else null,
            imageProcessingStartTime = null,
            onSettingsClick = {
                imageShown = !imageShown
            },
            modifier = Modifier,
        )
    }
}