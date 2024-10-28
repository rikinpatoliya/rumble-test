package com.rumble.battles.notifications.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.commonViews.ToggleRowView
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationSettingsScreen(
    handler: NotificationSettingsHandler,
    contentHandler: ContentHandler,
    onBackClick: () -> Unit,
) {

    val state by handler.state

    LaunchedEffect(Unit) {
        handler.eventFlow.collectLatest { event ->
            when (event) {
                is NotificationSettingsEvent.Error -> {
                    contentHandler.onError(event.message)
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        val (topBar, content) = createRefs()
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.notification_settings),
            modifier = Modifier
                .constrainAs(topBar) { top.linkTo(parent.top) }
                .fillMaxWidth(),
            onBackClick = onBackClick,
        )

        state.notificationSettingsEntity?.let { notificationSettingsEntity ->
            BoxWithConstraints(modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(topBar.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        horizontal = CalculatePaddingForTabletWidth(
                            maxWidth = maxWidth
                        )
                    )
                ) {
                    item {
                        Spacer(Modifier.height(paddingLarge))

                        Row(
                            modifier = Modifier
                                .padding(
                                    start = paddingMedium,
                                    end = paddingMedium,
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(id = R.string.notifications),
                                style = h1
                            )
                            Spacer(
                                Modifier
                                    .weight(1f)
                            )
                            RumbleTextActionButton(
                                text = stringResource(id = if (state.allNotificationsEnabled) R.string.disable_all else R.string.enable_all)
                            ) {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        moneyEarned = state.allNotificationsEnabled.not(),
                                        videoApprovedForMonetization = state.allNotificationsEnabled.not(),
                                        someoneFollowsYou = state.allNotificationsEnabled.not(),
                                        someoneTagsYou = state.allNotificationsEnabled.not(),
                                        commentsOnYourVideo = state.allNotificationsEnabled.not(),
                                        repliesToYourComments = state.allNotificationsEnabled.not(),
                                        newVideoBySomeoneYouFollow = state.allNotificationsEnabled.not(),
                                    )
                                )
                            }
                        }
                        ToggleRowView(
                            text = stringResource(id = R.string.money_earned),
                            textStyle = body1,
                            checked = notificationSettingsEntity.moneyEarned,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        moneyEarned = it
                                    )
                                )
                            }
                        )
                        ToggleRowView(
                            text = stringResource(id = R.string.video_is_approved_for_monetization),
                            textStyle = body1,
                            checked = notificationSettingsEntity.videoApprovedForMonetization,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        videoApprovedForMonetization = it
                                    )
                                )
                            }
                        )
                        ToggleRowView(
                            text = stringResource(id = R.string.someone_follows_you),
                            textStyle = body1,
                            checked = notificationSettingsEntity.someoneFollowsYou,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        someoneFollowsYou = it
                                    )
                                )
                            }
                        )
                        ToggleRowView(
                            text = stringResource(id = R.string.someone_tags_you),
                            textStyle = body1,
                            checked = notificationSettingsEntity.someoneTagsYou,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        someoneTagsYou = it
                                    )
                                )
                            }
                        )
                        ToggleRowView(
                            text = stringResource(id = R.string.comments_on_your_video),
                            textStyle = body1,
                            checked = notificationSettingsEntity.commentsOnYourVideo,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        commentsOnYourVideo = it
                                    )
                                )
                            }
                        )
                        ToggleRowView(
                            text = stringResource(id = R.string.replies_to_your_comments),
                            textStyle = body1,
                            checked = notificationSettingsEntity.repliesToYourComments,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        repliesToYourComments = it
                                    )
                                )
                            }
                        )
                        ToggleRowView(
                            text = stringResource(id = R.string.new_video_by_someone_you_follow),
                            textStyle = body1,
                            checked = notificationSettingsEntity.newVideoBySomeoneYouFollow,
                            addSeparator = true,
                            onCheckedChange = {
                                handler.onToggleNotificationSettings(
                                    notificationSettingsEntity.copy(
                                        newVideoBySomeoneYouFollow = it
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}