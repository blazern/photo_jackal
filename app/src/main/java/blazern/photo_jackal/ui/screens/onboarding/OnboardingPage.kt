package blazern.photo_jackal.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R

@Composable
fun OnboardingPage(
    title: String,
    @DrawableRes image: Int,
    description: String,
    bottomContent: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(),
        )
        Image(
            painterResource(image),
            contentDescription = "",
            modifier = Modifier
                .weight(2f)
                .wrapContentHeight(),
        )
        Text(description, modifier = Modifier
            .weight(1f)
            .wrapContentHeight())
        bottomContent?.let {
            Box(modifier = Modifier
                .weight(1f)
                .wrapContentHeight()) {
                bottomContent.invoke()
            }
        }
    }
}

@Preview
@Composable
private fun OnboardingPagePreview() {
    Box(modifier = Modifier.background(Color.White)) {
        OnboardingPage(
            title = "Purpose of the App",
            R.drawable.icon,
            description = """
                Lorem ipsum dolor sit amet, consectetur adipiscing elit,
                 sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
                 Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris
                 nisi ut aliquip ex ea commodo consequat. 
            """.trimIndent(),
        ) {
            Button(onClick = {}) {
                Text(text = "Wow")
            }
        }
    }
}
