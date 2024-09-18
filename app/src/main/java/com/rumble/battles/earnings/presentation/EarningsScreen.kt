package com.rumble.battles.earnings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.battles.EarningsTag
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.earnings.presentation.views.EarningItemView
import com.rumble.theme.RumbleTypography.body1Bold
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXLarge
import com.rumble.utils.extension.toCurrencyString

@Composable
fun EarningsScreen(
    earningsHandler: EarningsHandler,
    onBackClick: () -> Unit,
) {
    val earningsState: State<EarningsState> =
        earningsHandler.state.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        earningsHandler.vmEvents.collect { event ->
            when (event) {
                is EarningsVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.generic_error_message_try_later)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .testTag(EarningsTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.earnings),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .systemBarsPadding(),
            onBackClick = onBackClick,
        )

        SwipeRefresh(
            modifier = Modifier.testTag(SwipeRefreshTag),
            state = rememberSwipeRefreshState(
                earningsState.value.loading
            ),
            onRefresh = { earningsHandler.refresh() },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            BoxWithConstraints {
                val horizontalContentPadding =
                    CalculatePaddingForTabletWidth(maxWidth, defaultPadding = paddingMedium)

                val earnings = earningsState.value.earnings
                Column(
                    modifier = Modifier
                        .padding(horizontal = horizontalContentPadding)
                        .verticalScroll(rememberScrollState())
                ) {

                    Text(
                        modifier = Modifier.padding(top = paddingLarge),
                        text = stringResource(id = R.string.estimated_earnings),
                        style = h1
                    )
                    EarningItemView(
                        label = stringResource(id = R.string.youtube_earnings),
                        value = earnings.youtube.toCurrencyString(earnings.currencySymbol),
                    )
                    EarningItemView(
                        label = stringResource(id = R.string.third_party_earnings),
                        value = earnings.partners.toCurrencyString(earnings.currencySymbol),
                    )
                    EarningItemView(
                        label = stringResource(id = R.string.rumble_earnings),
                        value = earnings.rumble.toCurrencyString(earnings.currencySymbol),
                    )

                    Divider(modifier = Modifier.padding(top = paddingMedium))

                    EarningItemView(
                        label = stringResource(id = R.string.total_earnings),
                        labelStyle = body1Bold,
                        value = earnings.total.toCurrencyString(earnings.currencySymbol),
                    )

                    Text(
                        modifier = Modifier.padding(top = paddingXXXLarge),
                        text = stringResource(id = R.string.your_videos),
                        style = h1
                    )
                    EarningItemView(
                        label = stringResource(id = R.string.uploaded_videos),
                        value = earnings.uploaded.toString()
                    )
                    EarningItemView(
                        label = stringResource(id = R.string.approved_videos),
                        value = earnings.approved.toString()
                    )
                    EarningItemView(
                        label = stringResource(id = R.string.approved_percentage),
                        value = stringResource(
                            id = R.string.percentage,
                            earnings.approvedPercentage
                        )
                    )
                }
            }
        }

    }
}