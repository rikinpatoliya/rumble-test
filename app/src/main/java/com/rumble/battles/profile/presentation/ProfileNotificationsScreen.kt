package com.rumble.battles.profile.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.rumble.battles.NotificationsTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.theme.RumbleTypography.smallBody
import com.rumble.theme.bottomBarSpacerBehind
import com.rumble.theme.notificationsVideoWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants.VIDEO_CARD_THUMBNAIL_ASPECT_RATION
import com.rumble.utils.extension.agoString

@Composable
fun ProfileNotificationsScreen(
    profileNotificationsHandler: ProfileNotificationsHandler,
    onBackClick: () -> Unit,
    onChannelClick: (id: String) -> Unit,
    onVideoClick: (videoEntity: VideoEntity) -> Unit
) {
    val state by profileNotificationsHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val notificationsList: LazyPagingItems<ProfileNotificationEntity> =
        profileNotificationsHandler.notificationsPagingDataFlow.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .testTag(NotificationsTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.notifications),
            modifier = Modifier
                .fillMaxWidth(),
            onBackClick = onBackClick,
        )

        if (notificationsList.itemCount == 0) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = bottomBarSpacerBehind)
            ) {
                EmptyView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            vertical = paddingMedium,
                            horizontal = CalculatePaddingForTabletWidth(
                                maxWidth = maxWidth,
                                defaultPadding = paddingMedium
                            )
                        ),
                    title = stringResource(id = R.string.no_notifications_yet),
                    text = stringResource(id = R.string.you_will_receive_one_when_exciting_happens)
                )
            }
        } else {
            BoxWithConstraints {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = CalculatePaddingForTabletWidth(
                            maxWidth = maxWidth
                        )
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(notificationsList.itemCount) {
                        notificationsList[it]?.let { item ->
                            ProfileNotificationItem(item, context, onChannelClick, onVideoClick)
                        }
                    }
                    notificationsList.apply {
                        item {
                            when {
                                loadState.append is LoadState.Loading -> {
                                    PageLoadingView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(paddingMedium)
                                    )
                                }

                                loadState.append is LoadState.Error -> {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(paddingMedium),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = ::retry,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        BottomNavigationBarScreenSpacer()
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

@Composable
fun ProfileNotificationItem(
    entity: ProfileNotificationEntity,
    context: Context,
    onChannelClick: (id: String) -> Unit,
    onVideoClick: (videoEntity: VideoEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onChannelClick(entity.channelId) }
            .padding(
                top = paddingSmall,
                bottom = paddingSmall,
                start = paddingMedium,
                end = paddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImageComponent(
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXMediumStyle(),
            userName = entity.userName,
            userPicture = entity.userThumb,
        )
        Text(
            modifier = Modifier
                .padding(start = paddingSmall, end = paddingLarge)
                .weight(1F),
            text = buildStyledText(entity, context),
            style = smallBody
        )

        entity.videoEntity?.let { videoEntity ->
            Box(
                modifier = Modifier
                    .width(notificationsVideoWidth)
                    .aspectRatio(VIDEO_CARD_THUMBNAIL_ASPECT_RATION)
                    .clip(RoundedCornerShape(radiusMedium))
                    .background(MaterialTheme.colors.primaryVariant)
                    .clickable { onVideoClick(videoEntity) }
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = entity.videoEntity?.videoThumbnail,
                    contentDescription = "",
                    contentScale = ContentScale.FillWidth
                )
            }

        }
    }
}

@Composable
private fun buildStyledText(
    entity: ProfileNotificationEntity,
    context: Context
): AnnotatedString =
    buildAnnotatedString {
        withStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold)
        ) {
            append(entity.userName)
        }
        append(" ")
        append(entity.message)
        append(" ")
        withStyle(style = SpanStyle(color = MaterialTheme.colors.secondary)) {
            append(entity.timeAgo.agoString(context))
        }
    }