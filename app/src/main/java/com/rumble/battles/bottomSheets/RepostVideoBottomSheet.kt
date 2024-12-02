package com.rumble.battles.bottomSheets

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.ChannelSelectionBottomSheet
import com.rumble.battles.commonViews.ChannelSelectionBottomSheetItem
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleTextInputField
import com.rumble.battles.commonViews.SelectChannelRowView
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RepostVideoBottomSheet(
    contentHandler: ContentHandler,
    videoEntity: VideoEntity,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val state by contentHandler.repostState.collectAsStateWithLifecycle()
    val userUIState by contentHandler.userUIState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch { bottomSheetState.hide() }
        } else {
            onClose()
        }
    }

    LaunchedEffect(Unit) {
        contentHandler.resetRepostState()
    }

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            ChannelSelectionBottomSheet(
                title = stringResource(id = R.string.select_channel),
                sheetItems = buildChannelSelectionBottomSheetItems(
                    userUIState.userChannel,
                    userUIState.userUploadChannels,
                    state.selectedRepostChannelEntity,
                    onChannelSelected = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                            contentHandler.onRepostOwnerChanged(it)
                        }
                    },
                    context
                ),
                onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
            )
        }) {
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
                .background(RumbleCustomTheme.colors.surface)
                .fillMaxSize()
        ) {
            var spacerHeight by remember { mutableIntStateOf(0) }
            val density = LocalContext.current.resources.displayMetrics.density
            Column {
                Row(
                    modifier = Modifier
                        .padding(top = paddingMedium, start = paddingMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(R.string.repost_video),
                        color = RumbleCustomTheme.colors.primary,
                        style = RumbleTypography.h3
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
                if (userUIState.userUploadChannels.isNotEmpty()) {
                    Text(
                        text = stringResource(id = R.string.channel).uppercase(),
                        modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
                        color = MaterialTheme.colors.primary,
                        style = RumbleTypography.h6Heavy
                    )
                    SelectChannelRowView(
                        title = state.selectedRepostChannelEntity.title,
                        description = stringResource(id = R.string.username_prefix) + state.selectedRepostChannelEntity.name
                    ) {
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    }
                }
                RumbleTextInputField(
                    initialValue = state.post,
                    label = stringResource(id = R.string.post),
                    maxLines = 4,
                    maxCharacters = RumbleConstants.MAX_CHARACTERS_REPOST,
                    onValueChange = { contentHandler.onRepostTextChanged(it) },
                    hasError = state.repostError,
                    errorMessage = stringResource(id = R.string.error_message_repost_text_too_long)
                )
                Box(
                    modifier = Modifier
                        .padding(paddingMedium)
                        .clip(RoundedCornerShape(radiusMedium))
                        .border(
                            borderXXSmall,
                            color = RumbleCustomTheme.colors.backgroundHighlight,
                            shape = RoundedCornerShape(radiusMedium)
                        )
                ) {
                    VideoCompactView(
                        modifier = Modifier
                            .padding(paddingXSmall),
                        videoEntity = videoEntity,
                        showMoreAction = false,
                        onMoreClick = { },
                        onImpression = { },
                    )
                }
                Spacer(modifier = Modifier.height((spacerHeight * 1.5 / density).dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RumbleCustomTheme.colors.surface)
                    .padding(horizontal = paddingMedium, vertical = paddingLarge)
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned {
                        spacerHeight = it.size.height
                    }
            ) {
                MainActionButton(
                    textModifier = Modifier.padding(
                        horizontal = paddingMedium,
                        vertical = paddingXSmall
                    ),
                    text = context.getString(R.string.cancel),
                    backgroundColor = MaterialTheme.colors.onSecondary,
                    onClick = onClose
                )
                MainActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = paddingMedium),
                    textModifier = Modifier.padding(vertical = paddingXSmall),
                    text = context.getString(R.string.repost),
                    textColor = enforcedDarkmo,
                    onClick = {
                        if (state.repostError.not()) {
                            contentHandler.onRepost(
                                videoId = videoEntity.id,
                                channelId = state.selectedRepostChannelEntity.channelId,
                                message = state.post
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun buildChannelSelectionBottomSheetItems(
    userChannel: UserUploadChannelEntity,
    userUploadChannels: List<UserUploadChannelEntity>,
    selectedRepostChannelEntity: UserUploadChannelEntity,
    onChannelSelected: (UserUploadChannelEntity) -> Unit,
    context: Context

): List<ChannelSelectionBottomSheetItem> {
    val result = mutableListOf<ChannelSelectionBottomSheetItem>()
    result.add(
        ChannelSelectionBottomSheetItem(
            imageUrl = userChannel.thumbnail ?: "",
            text = userChannel.name,
            subText = context.getString(R.string.username_prefix) + userChannel.name,
            selected = selectedRepostChannelEntity.id == userChannel.id,
            action = { onChannelSelected(userChannel) }
        )
    )
    result.addAll(
        userUploadChannels.map { userUploadChannelEntity ->
            ChannelSelectionBottomSheetItem(
                imageUrl = userUploadChannelEntity.thumbnail ?: "",
                text = userUploadChannelEntity.title,
                subText = context.getString(R.string.channel_name_prefix) + userUploadChannelEntity.name,
                selected = selectedRepostChannelEntity.id == userUploadChannelEntity.id,
                action = { onChannelSelected(userUploadChannelEntity) }
            )
        }
    )
    return result
}