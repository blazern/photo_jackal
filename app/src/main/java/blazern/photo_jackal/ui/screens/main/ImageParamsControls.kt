package blazern.photo_jackal.ui.screens.main

import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import blazern.photo_jackal.ui.theme.PhotoJackalTheme


@Composable
fun ImageParamsControls(
    imageCompressLevel: Float,
    compressedImageResolution: Size?,
    selectedImageResolution: Size?,
    compressedImageMinimalResolution: Size?,
    imageResolutionScale: Float,
    onCompressLevelChange: (Float)->Unit,
    onResolutionScaleChange: (Float)->Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.compression_level),
            style = MaterialTheme.typography.titleMedium,
        )
        Row {
            Image(painterResource(R.drawable.jackal_4), "", modifier = Modifier.weight(1f))
            Slider(
                value = imageCompressLevel,
                onValueChange = { onCompressLevelChange(it) },
                modifier = Modifier.weight(6f)
            )
            Image(painterResource(R.drawable.jackal_0), "", modifier = Modifier.weight(1f))
        }
        Box(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = stringResource(R.string.resolution) + " ",
                style = MaterialTheme.typography.titleMedium,
            )
            if (compressedImageResolution != null) {
                Text(
                    text = "${compressedImageResolution.width}×${compressedImageResolution.height}",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        val imageWidth = selectedImageResolution?.width
        val imageHeight = selectedImageResolution?.height
        val minWidth = compressedImageMinimalResolution?.width
        val minHeight = compressedImageMinimalResolution?.height
        val haveResolutions = imageWidth != null && imageHeight != null && minWidth != null && minHeight != null
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (haveResolutions) {
                Icon(Icons.Default.Remove, "", modifier = Modifier.weight(1f))
            }
            Slider(
                value = imageResolutionScale,
                onValueChange = { onResolutionScaleChange.invoke(it) },
                modifier = Modifier.weight(6f),
            )
            if (haveResolutions) {
                Icon(Icons.Default.Add, "", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PhotoJackalTheme {
        ImageParamsControls(
            selectedImageResolution = Size(1000, 1000),
            compressedImageResolution = Size(50, 50),
            compressedImageMinimalResolution = Size(10, 10),
            imageCompressLevel = 0.5f,
            imageResolutionScale = 0.5f,
            onCompressLevelChange = {},
            onResolutionScaleChange = {},
            modifier = Modifier
        )
    }
}