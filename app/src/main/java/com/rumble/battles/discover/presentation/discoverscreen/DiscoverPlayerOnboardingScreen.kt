package com.rumble.battles.discover.presentation.discoverscreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.theme.*
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverPlayerOnboardingScreen() {
    val items = OnBoardingItem.getData()
    val scope = rememberCoroutineScope()

    val pageState = rememberPagerState { items.size }

    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .background(color = enforcedDarkmo.copy(alpha = 0.8F))
    ) {
        RumbleTextActionButton(
            modifier = Modifier
                .padding(paddingLarge)
                .align(Alignment.TopEnd),
            text = stringResource(id = R.string.skip),
            textColor = enforcedWhite,
            textStyle = h6Light.copy(textAlign = TextAlign.End)
        ) {
            if (pageState.currentPage + 1 < items.size) scope.launch {
                pageState.scrollToPage(items.size - 1)
            }
        }

        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) { page ->
            OnBoardingCard(onBoardingItem = items[page])
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = paddingXXXLarge)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Indicators(items.size, pageState.currentPage)
            Row(
                modifier = Modifier.padding(top = paddingLarge),
                horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RumbleTextActionButton(
                    text = stringResource(id = if (items.size == pageState.currentPage + 1) R.string.watch_videos else R.string.next),
                    textColor = if (items.size == pageState.currentPage + 1) rumbleGreen else enforcedWhite,
                    textStyle = if (items.size == pageState.currentPage + 1) h6Light else h6
                ) {
                    if (pageState.currentPage + 1 < items.size) scope.launch {
                        pageState.scrollToPage(pageState.currentPage + 1)
                    }
                }
                if (items.size == pageState.currentPage + 1) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_forward),
                        contentDescription = stringResource(id = R.string.watch_videos),
                        modifier = Modifier.size(imageXXXXSmall)
                    )
                }
            }
        }
    }

}

@Composable
fun Indicators(size: Int, index: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall),
    ) {
        repeat(size) {
            Indicator(isSelected = it == index)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(
        targetValue = if (isSelected) imageMedium else imageXXXXSmall,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .height(imageXXXXSmall)
            .width(width.value)
            .clip(CircleShape)
            .background(
                color = if (isSelected) rumbleGreen else enforcedWhite.copy(alpha = 0.2F)
            )
    ) {}
}

@Composable
fun OnBoardingCard(onBoardingItem: OnBoardingItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = onBoardingItem.title),
            style = h1,
            color = enforcedWhite,
            textAlign = TextAlign.Center,
        )

        when (onBoardingItem) {
            is OnBoardingItem.OneImageOnboarding -> {
                Image(
                    painter = painterResource(id = onBoardingItem.image),
                    contentDescription = stringResource(id = onBoardingItem.description),
                    modifier = Modifier.padding(paddingLarge)
                )

                Text(
                    text = stringResource(id = onBoardingItem.description),
                    style = h3,
                    color = enforcedWhite,
                    textAlign = TextAlign.Center,
                )
            }
            is OnBoardingItem.TwoImageOnboarding -> {
                Row(
                    modifier = Modifier.padding(paddingLarge),
                    horizontalArrangement = Arrangement.spacedBy(paddingLarge),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = onBoardingItem.image1),
                            contentDescription = stringResource(id = onBoardingItem.description1),
                        )
                        Text(
                            text = stringResource(id = onBoardingItem.description1),
                            modifier = Modifier.padding(top = paddingSmall, start = paddingXSmall),
                            style = h3,
                            color = fierceRed,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Column {
                        Image(
                            painter = painterResource(id = onBoardingItem.image2),
                            contentDescription = stringResource(id = onBoardingItem.description2),
                        )

                        Text(
                            text = stringResource(id = onBoardingItem.description2),
                            modifier = Modifier.padding(top = paddingSmall, start = paddingXSmall),
                            style = h3,
                            color = rumbleGreen,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

            }
        }

    }
}

sealed class OnBoardingItem(open val title: Int) {
    data class OneImageOnboarding(
        val image: Int,
        override val title: Int,
        val description: Int
    ) : OnBoardingItem(title = title)

    data class TwoImageOnboarding(
        val image1: Int,
        val image2: Int,
        override val title: Int,
        val description1: Int,
        val description2: Int
    ) : OnBoardingItem(title = title)

    companion object {
        fun getData(): List<OnBoardingItem> {
            return listOf(
                TwoImageOnboarding(
                    image1 = R.drawable.hand_onefinger_swipe_left,
                    image2 = R.drawable.hand_onefinger_swipe_right,
                    R.string.swipe,
                    description1 = R.string.dislike,
                    description2 = R.string.like
                ),
                OneImageOnboarding(
                    image = R.drawable.hand_onefinger_scroll_down,
                    title = R.string.swipe,
                    description = R.string.up_for_next_down_for_previous
                ),
                OneImageOnboarding(
                    image = R.drawable.hand_onefinger_tap,
                    title = R.string.tap,
                    description = R.string.to_pause_play_video
                )
            )
        }
    }
}
