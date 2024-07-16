package blazern.photo_jackal.test_utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeWithVelocity

fun SemanticsNodeInteraction.swipeLeft() {
    performTouchInput {
        swipeWithVelocity(
            start = Offset(100f, 100f),
            end = Offset(x = 0f, y = 0f),
            endVelocity = 4000f,
        )
    }
}

fun SemanticsNodeInteraction.swipeRight() {
    performTouchInput {
        swipeWithVelocity(
            start = Offset(100f, 100f),
            end = Offset(x = 200f, y = 0f),
            endVelocity = 4000f,
        )
    }
}
