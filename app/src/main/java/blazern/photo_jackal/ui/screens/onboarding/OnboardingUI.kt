package blazern.photo_jackal.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blazern.photo_jackal.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingUI(finish: () -> Unit, openPrivacyPolicy: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val canFinish = remember {
        derivedStateOf {
            pagerState.currentPage == pagerState.pageCount - 1
        }
    }
    val canMoveBackward by remember {
        derivedStateOf {
            0 < pagerState.currentPage
        }
    }
    val offsetPage = { offset: Int ->
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + offset)
        }
    }

    Box(Modifier
        .padding(bottom = 32.dp)
        .testTag("onboarding_pages")
    ) {
        Pages(pagerState, openPrivacyPolicy)
    }
    Controls(offsetPage, canMoveBackward, canFinish, finish)
}

@Composable
private fun Controls(
    offsetPage: (Int) -> Job,
    canMoveBackward: Boolean,
    canFinish: State<Boolean>,
    finish: () -> Unit
) {
    Column {
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            FilledTonalButton(
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                onClick = { offsetPage(-1) },
                enabled = canMoveBackward,
            ) {
                Text(stringResource(R.string.onboarding_back))
            }
            Spacer(Modifier.weight(1f))
            FilledTonalButton(
                modifier = Modifier.padding(end = 16.dp, bottom = 8.dp),
                onClick = {
                    if (canFinish.value) {
                        finish()
                    } else {
                        offsetPage(+1)
                    }
                },
            ) {
                val str = if (canFinish.value) {
                    R.string.onboarding_finish
                } else {
                    R.string.onboarding_next
                }
                Text(stringResource(str))
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Pages(pagerState: PagerState, openPrivacyPolicy: () -> Unit) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> {
                OnboardingPage(
                    title = "",
                    R.drawable.jackal_incognito,
                    description = stringResource(R.string.onboarding1_descr),
                ) {
                    Button(onClick = openPrivacyPolicy) {
                        Text(text = stringResource(R.string.onboarding1_privacy_policy_button))
                    }
                }
            }

            1 -> {
                OnboardingPage(
                    title = stringResource(R.string.onboarding2_title),
                    R.drawable.jackal_pixelized,
                    description = stringResource(R.string.onboarding2_descr),
                )
            }

            2 -> {
                OnboardingPage(
                    title = stringResource(R.string.onboarding3_title),
                    R.drawable.jackal_laptop,
                    description = stringResource(R.string.onboarding3_descr),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 400)
@Composable
fun OnboardingUIPreview() {
    OnboardingUI({}, {})
}
