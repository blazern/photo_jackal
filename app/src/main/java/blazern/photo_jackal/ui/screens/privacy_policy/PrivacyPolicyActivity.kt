package blazern.photo_jackal.ui.screens.privacy_policy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.ui.theme.PhotoJackalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyPolicyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoJackalTheme {
                val coroutineScope = rememberCoroutineScope()
                var textFromFile by remember { mutableStateOf("") }

                // Trigger file reading asynchronously
                LaunchedEffect(key1 = "readText") {
                    coroutineScope.launch {
                        textFromFile = readTextFromAssets()
                    }
                }

                Column(modifier =
                Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(rememberScrollState()))
                {
                    Text(text = textFromFile)
                }
            }
        }
    }

    private suspend fun readTextFromAssets(): String = withContext(Dispatchers.IO) {
        assets.open("privacy_policy.md").bufferedReader().use { it  .readText() }
    }
}