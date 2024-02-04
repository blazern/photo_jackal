package blazern.photo_jackal.ui.screens.main

import android.net.Uri
import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImagePicker
import coil.compose.AsyncImage

@Composable
fun MainScreenUI(
    state: State<MainScreenState>,
    onCompressLevelChange: (Float)->Unit,
    onResolutionScaleChange: (Float)->Unit,
    onUserPickedImage: (Uri?)->Unit,
    onShareImageClick: ()->Unit,
    onRemoveImageCLick: ()->Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(10f),
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    val compressedImageUri by remember { derivedStateOf { state.value.compressedImage } }
                    if (compressedImageUri != null) {
                        Box {
                            AsyncImage(
                                model = compressedImageUri,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = stringResource(R.string.selected_image),
                            )
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
                    }
                    val processing by remember { derivedStateOf { state.value.processingImage } }
                    if (processing) {
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
            }
            val controlsAlpha by remember { derivedStateOf {
                if (state.value.selectedImage != null) 1f else 0f
            } }
            Column(modifier = Modifier
                .weight(4f)
                .alpha(controlsAlpha)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.Center)
            {
                Text(text = stringResource(R.string.compression_level))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Image(painterResource(R.drawable.jackal_4), "", modifier = Modifier.weight(1f))
                    Image(painterResource(R.drawable.jackal_3), "", modifier = Modifier.weight(1f))
                    Image(painterResource(R.drawable.jackal_2), "", modifier = Modifier.weight(1f))
                    Image(painterResource(R.drawable.jackal_1), "", modifier = Modifier.weight(1f))
                    Image(painterResource(R.drawable.jackal_0), "", modifier = Modifier.weight(1f))
                }
                val compressLevel by remember { derivedStateOf { state.value.imageCompressLevel } }
                Slider(
                    value = compressLevel,
                    onValueChange = { onCompressLevelChange(it) }
                )
                val compressedImageResolution by remember { derivedStateOf { state.value.compressedImageResolution } }
                Row {
                    Text(text = stringResource(R.string.resolution) + " ")
                    val compressedWidth = compressedImageResolution?.width
                    val compressedHeight = compressedImageResolution?.height
                    if (compressedWidth != null && compressedHeight != null) {
                        Text(text = "${compressedWidth}x$compressedHeight")
                    }
                }
                val imageResolution by remember { derivedStateOf { state.value.selectedImageResolution } }
                val imageMinResolution by remember { derivedStateOf { state.value.compressedImageMinimalResolution } }
                val imageWidth = imageResolution?.width
                val imageHeight = imageResolution?.height
                val minWidth = imageMinResolution?.width
                val minHeight = imageMinResolution?.height
                if (imageWidth != null && imageHeight != null && minWidth != null && minHeight != null) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val maxResolutionStr = "${imageWidth}x${imageHeight}"
                        val minResolutionStr = "${minWidth}x${minHeight}"
                        Text(
                            minResolutionStr,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            maxResolutionStr,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                val resolutionScale by remember { derivedStateOf { state.value.imageResolutionScale } }
                Slider(
                    value = resolutionScale,
                    onValueChange = { onResolutionScaleChange.invoke(it) }
                )
            }
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