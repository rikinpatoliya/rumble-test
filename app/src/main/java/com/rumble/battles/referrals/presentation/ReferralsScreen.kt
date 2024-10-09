package com.rumble.battles.referrals.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.R
import com.rumble.battles.ReferralsTag
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.referrals.presentation.views.ReferralDetailsView
import com.rumble.battles.referrals.presentation.views.ReferralShareView
import com.rumble.battles.referrals.presentation.views.ReferralView
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.paddingMedium
import kotlinx.coroutines.launch

@Composable
fun ReferralsScreen(
    handler: ReferralsHandler,
    onBackClick: () -> Unit,
    onChannelClick: (channelId: String) -> Unit
) {
    val state by handler.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val linkCopiedToClipboard = stringResource(R.string.link_copied_to_clipboard)

    ConstraintLayout(
        modifier = Modifier
            .testTag(ReferralsTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        val (topAppBar, listGroup, shareGroup, snackBarHost) = createRefs()

        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.referrals),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .constrainAs(topAppBar) { top.linkTo(parent.top) },
            onBackClick = onBackClick,
        )

        SwipeRefresh(
            modifier = Modifier
                .testTag(SwipeRefreshTag)
                .fillMaxWidth()
                .constrainAs(listGroup) {
                    top.linkTo(topAppBar.bottom)
                    bottom.linkTo(shareGroup.top)
                    height = Dimension.fillToConstraints
                },
            state = rememberSwipeRefreshState(state.loading),
            onRefresh = { handler.refresh() },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            BoxWithConstraints {
                LazyColumn(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(
                        vertical = paddingMedium,
                        horizontal = CalculatePaddingForTabletWidth(
                            maxWidth = maxWidth,
                            defaultPadding = paddingMedium
                        )
                    )
                ) {

                    state.referralDetailsEntity?.let { referralDetails ->
                        item {
                            ReferralDetailsView(referralDetails)
                        }

                        item {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = paddingMedium),
                                text = stringResource(id = R.string.referred_users),
                                style = h3
                            )
                        }

                        if (referralDetails.referrals.isNotEmpty()) {
                            itemsIndexed(referralDetails.referrals) { index, item ->
                                ReferralView(
                                    referral = item,
                                    backgroundColor = referralViewBackgroundColor(index),
                                    onChannelClick = onChannelClick
                                )
                            }
                        } else {
                            item {
                                EmptyView(
                                    modifier = Modifier
                                        .fillParentMaxHeight(0.5f)
                                        .fillMaxWidth(),
                                    title = stringResource(id = R.string.you_have_not_referred_anyone_yet),
                                    text = stringResource(id = R.string.share_the_link)
                                )
                            }
                        }
                    }
                }
            }
        }

        RumbleSnackbarHost(
            modifier = Modifier.constrainAs(snackBarHost) { bottom.linkTo(shareGroup.top) },
            snackBarHostState = snackBarHostState
        )

        Box(modifier = Modifier
            .systemBarsPadding()
            .constrainAs(shareGroup) {
                bottom.linkTo(parent.bottom)
            }
        ) {

            ReferralShareView(
                referralUrl = state.referralUrl,
                onShare = (handler::share),
                onCopy = {
                    coroutineScope.launch {
                        snackBarHostState.showRumbleSnackbar(message = linkCopiedToClipboard)
                    }
                }
            )
        }
    }
}

@Composable
fun referralViewBackgroundColor(index: Int): Color {
    return if (index % 2 == 0) {
        MaterialTheme.colors.surface
    } else {
        MaterialTheme.colors.onSecondary
    }
}

