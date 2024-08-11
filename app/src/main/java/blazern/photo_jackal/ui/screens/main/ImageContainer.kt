package blazern.photo_jackal.ui.screens.main

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
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.ImageDebugPlaceholder
import coil.compose.AsyncImage


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
        Box(modifier = Modifier.fillMaxSize()) {
            if (compressedImageUri != null) {
                AsyncImage(
                    model = compressedImageUri,
                    placeholder = ImageDebugPlaceholder(R.drawable.placeholder_flat),
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
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                onClick = onSettingsClick
            )
            {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
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
