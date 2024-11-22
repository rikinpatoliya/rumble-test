package com.rumble.battles.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.CheckBoxView
import com.rumble.battles.commonViews.RumbleRadioSelectionRow
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.notifications.presentation.views.NotificationItemView
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.sort.NotificationFrequency
import com.rumble.network.queryHelpers.Frequency
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXXLarge
import com.rumble.theme.radiusXMedium
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChannelNotificationsBottomSheet(
    channelDetailsEntity: CreatorEntity,
    contentHandler: ContentHandler,
    onHideBottomSheet: () -> Unit
) {
    val notificationFrequencyList = NotificationFrequency.emailFrequencyTypes
    var channel by remember { mutableStateOf(channelDetailsEntity) }

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.ChannelNotificationsUpdated) {
                channel = it.channelDetailsEntity
            }
        }
    }

    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(RumbleCustomTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(paddingSmall))

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.notifications),
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .align(Alignment.CenterStart),
                    color = RumbleCustomTheme.colors.primary,
                    style = RumbleTypography.h3
                )
                IconButton(
                    onClick = onHideBottomSheet,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close),
                        tint = RumbleCustomTheme.colors.primary
                    )
                }
            }
            NotificationItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = paddingXXMedium,
                        horizontal = paddingMedium
                    ),
                iconId = R.drawable.ic_streams,
                labelId = R.string.push_notifications_for_streams,
                descriptionId = R.string.get_notified_as_they_go_live,
                onClick = {
                    contentHandler.onEnablePushForLivestreams(
                        channel,
                        channel.pushNotificationsEnabled.not()
                    )
                },
                trailingView = {
                    CheckBoxView(checked = channel.pushNotificationsEnabled) {
                        contentHandler.onEnablePushForLivestreams(channel, it)
                    }
                }
            )
            Divider(
                color = RumbleCustomTheme.colors.backgroundHighlight
            )
            NotificationItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = paddingXXMedium,
                        horizontal = paddingMedium
                    ),
                iconId = R.drawable.ic_notifications,
                labelId = R.string.email_notifications,
                descriptionId = R.string.get_regular_emails,
                onClick = {
                    contentHandler.onEnableEmailNotifications(
                        channel,
                        channel.emailNotificationsEnabled.not()
                    )
                },
                trailingView = {
                    CheckBoxView(checked = channel.emailNotificationsEnabled) {
                        contentHandler.onEnableEmailNotifications(channel, it)
                    }
                }
            )
            Divider(
                color = RumbleCustomTheme.colors.backgroundHighlight
            )
            if (channel.emailNotificationsEnabled) {
                Column(
                    modifier = Modifier.padding(
                        start = paddingXXXLarge,
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(
                            top = paddingMedium,
                            bottom = paddingSmall,
                            start = paddingSmall,
                            end = paddingMedium
                        ),
                        text = stringResource(id = R.string.how_frequently_receive_notifications),
                        color = RumbleCustomTheme.colors.primary,
                        style = h5
                    )
                    notificationFrequencyList.forEach { notificationFrequency ->
                        RumbleRadioSelectionRow(
                            modifier = Modifier.padding(
                                top = paddingMedium,
                                bottom = paddingMedium,
                                start = paddingSmall,
                                end = paddingMedium
                            ),
                            title = stringResource(id = notificationFrequency.nameId),
                            selected = notificationFrequency.frequency.value == channel.emailNotificationsFrequency,
                            onSelected = {
                                contentHandler.onUpdateEmailFrequency(
                                    channel,
                                    notificationFrequency
                                )
                            }
                        )
                        if (notificationFrequency.frequency != Frequency.MONTHLY) {
                            Divider(
                                color = RumbleCustomTheme.colors.backgroundHighlight
                            )
                        }
                    }
                }
                Divider(
                    color = RumbleCustomTheme.colors.backgroundHighlight
                )
            }
            NotificationItemView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = paddingXXMedium,
                        horizontal = paddingMedium
                    ),
                iconId = R.drawable.ic_unfollow,
                labelId = R.string.unfollow,
                descriptionId = R.string.no_longer_see_content,
                onClick = {
                    contentHandler.onUpdateSubscription(
                        channel,
                        UpdateChannelSubscriptionAction.UNSUBSCRIBE
                    )
                },
            )
            Spacer(modifier = Modifier.height(paddingLarge))
        }
    }
}