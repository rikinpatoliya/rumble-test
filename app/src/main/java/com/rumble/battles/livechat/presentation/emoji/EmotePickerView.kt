package com.rumble.battles.livechat.presentation.emoji

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.battles.feed.presentation.videodetails.EmoteState
import com.rumble.battles.livechat.presentation.LiveChatHandler
import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.domain.livechat.domain.domainmodel.EmoteGroupEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.borderXXSmall
import com.rumble.theme.emojiTabSelectorHeight
import com.rumble.theme.emotePickerHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.RumbleConstants.EMOTES_CONTROL_VISIBILITY_THRESHOLD
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun EmotePickerView(
    modifier: Modifier = Modifier,
    emoteState: State<EmoteState>,
    liveChatHandler: LiveChatHandler,
    onSwitchToKeyboard: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSelectEmote: (EmoteEntity) -> Unit = {},
    onDismissRemoteRequest: () -> Unit = {},
    onFollow: () -> Unit = {},
) {
    val context = LocalContext.current
    val state by remember { emoteState }
    val liveChatSate by remember { liveChatHandler.state }
    val emoteGroupList = liveChatSate.liveChatConfig?.emoteGroups ?: emptyList()
    val recentEmoteList = liveChatSate.recentEmoteList
    val tabs = createTabList(context, recentEmoteList, emoteGroupList)
    var selectedTab by remember { mutableStateOf(tabs.firstOrNull()) }
    val coroutineScope = rememberCoroutineScope()
    val contentListState = rememberLazyListState()
    val scrollOffset = with(LocalDensity.current) { paddingXSmall.toPx() }.toInt()

    LaunchedEffect(contentListState, tabs) {
        snapshotFlow { contentListState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                selectedTab = tabs[index]
            }
    }

    Column(
        modifier = modifier
            .wrapContentHeight()
            .background(RumbleCustomTheme.colors.background)
    ) {
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = borderXXSmall,
            color = RumbleCustomTheme.colors.backgroundHighlight,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(emotePickerHeight)
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = paddingSmall),
                contentPadding = PaddingValues(horizontal = paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(paddingSmall),
                state = contentListState,
            ) {
                if (recentEmoteList.isNotEmpty()) {
                    item {
                        EmoteGroupView(
                            modifier = Modifier.wrapContentWidth(),
                            title = stringResource(R.string.recent_emotes),
                            emoteList = recentEmoteList,
                            onSelectEmote = onSelectEmote,
                        )
                    }
                }
                items(emoteGroupList) {
                    EmoteGroupView(
                        modifier = Modifier.wrapContentWidth(),
                        title = it.title,
                        emoteList = it.emoteList,
                        onSelectEmote = onSelectEmote,
                    )
                }
            }

            if (tabs.size > EMOTES_CONTROL_VISIBILITY_THRESHOLD) {
                EmoteTabIndicator(
                    modifier = Modifier
                        .padding(horizontal = paddingMedium, vertical = paddingXXXSmall)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    totalCount = tabs.size,
                    selection = selectedTab?.index ?: 0,
                )
            }

            this@Column.AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                visible = state.requestFollow,
                content = {
                    EmoteRequestFollowView(
                        modifier = Modifier.fillMaxSize(),
                        channelName = state.channelName,
                        emoteEntity = state.requestedEmote,
                        emoteCount = liveChatHandler.getEmoteCountInSameGroup(state.requestedEmote),
                        onFollow = onFollow,
                        onBack = onDismissRemoteRequest,
                    )
                },
                enter = slideInHorizontally(animationSpec = tween(easing = LinearEasing)),
                exit = slideOutHorizontally(animationSpec = tween(easing = LinearEasing))
            )

            this@Column.AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                visible = state.requestSubscribe,
                content = {
                    EmoteRequestSubscribeView(
                        modifier = Modifier.fillMaxSize(),
                        channelName = state.channelName,
                        emoteEntity = state.requestedEmote,
                        onBack = onDismissRemoteRequest,
                    )
                },
                enter = slideInHorizontally(animationSpec = tween(easing = LinearEasing)),
                exit = slideOutHorizontally(animationSpec = tween(easing = LinearEasing))
            )
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = borderXXSmall,
            color = RumbleCustomTheme.colors.backgroundHighlight,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIconButton(
                modifier = Modifier,
                size = emojiTabSelectorHeight,
                painter = painterResource(id = R.drawable.ic_keyboard),
                backgroundColor = RumbleCustomTheme.colors.backgroundHighlight,
                tintColor = RumbleCustomTheme.colors.primary,
                contentDescription = stringResource(id = R.string.switch_to_keyboard),
                onClick = onSwitchToKeyboard
            )

            Spacer(modifier = Modifier.weight(1f))

            if (tabs.size > EMOTES_CONTROL_VISIBILITY_THRESHOLD) {
                EmoteTabSelector(
                    selectedTab = selectedTab,
                    tabList = tabs,
                    onTabSelected = {
                        selectedTab = it
                        coroutineScope.launch {
                            contentListState.animateScrollToItem(
                                index = it.index,
                                scrollOffset = scrollOffset
                            )
                        }
                        onDismissRemoteRequest()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            RoundIconButton(
                modifier = Modifier,
                size = emojiTabSelectorHeight,
                painter = painterResource(id = R.drawable.ic_backspace),
                backgroundColor = RumbleCustomTheme.colors.backgroundHighlight,
                tintColor = RumbleCustomTheme.colors.primary,
                contentDescription = stringResource(id = R.string.delete),
                onClick = onDelete,
            )
        }
    }
}

private fun createTabList(
    context: Context,
    recentEmoteList: List<EmoteEntity>,
    emoteGroupList: List<EmoteGroupEntity>,
): List<EmoteTab> {
    val tabList: MutableList<EmoteTab> = mutableListOf()
    if (recentEmoteList.isNotEmpty()) {
        tabList.add(
            EmoteTab(
                id = 0,
                index = 0,
                pictureId = R.drawable.ic_time,
                channelName = context.getString(R.string.recent_emotes),
            )
        )
    }
    emoteGroupList.forEach { group ->
        tabList.add(
            EmoteTab(
                id = group.id,
                index = tabList.size,
                pictureUrl = group.picture,
                channelName = group.title,
            )
        )
    }
    return tabList
}
