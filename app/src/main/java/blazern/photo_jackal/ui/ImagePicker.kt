package blazern.photo_jackal.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import blazern.photo_jackal.MyFileProvider
import blazern.photo_jackal.R

@Composable
fun ImagePicker(
    modifier: Modifier = Modifier,
    onImagePicked: (Uri?)->Unit
) {
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            onImagePicked.invoke(uri)
        }
    )

    var cameraImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                onImagePicked.invoke(cameraImageUri)
            } else {
                onImagePicked.invoke(null)
            }
        }
    )

    val context = LocalContext.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                imagePicker.launch("image/*")
            },
        ) {
            Text(text = stringResource(R.string.select_image))
        }
        Button(
            onClick = {
                val uri = MyFileProvider.getImageUri(context)
                cameraImageUri = uri
                cameraLauncher.launch(uri)
            },
        ) {
            Text(text = stringResource(R.string.take_photo))
        }
    }
}
