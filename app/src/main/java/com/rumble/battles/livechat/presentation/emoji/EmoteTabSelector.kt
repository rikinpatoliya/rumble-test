package com.rumble.battles.livechat.presentation.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.borderXXSmall
import com.rumble.theme.emojiTabHeight
import com.rumble.theme.emojiTabSelectorHeight
import com.rumble.theme.emojiTabWidth
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXXXMedium
import com.rumble.utils.extension.conditional

data class EmoteTab(
    val id: Long,
    val index: Int,
    val pictureId: Int? = null,
    val channelName: String? = null,
    val pictureUrl: String? = null,
)

@Composable
fun EmoteTabSelector(
    modifier: Modifier = Modifier,
    selectedTab: EmoteTab?,
    tabList: List<EmoteTab>,
    onTabSelected: (EmoteTab) -> Unit = {},
) {

    Row(
        modifier = modifier
            .height(emojiTabSelectorHeight)
            .clip(RoundedCornerShape(radiusXXXMedium))
            .background(RumbleCustomTheme.colors.subtleHighlight)
            .border(
                width = borderXXSmall,
                color = RumbleCustomTheme.colors.backgroundHighlight,
                shape = RoundedCornerShape(radiusXXXMedium)
            )
            .padding(horizontal = paddingXXXXSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabList.forEach { tab ->
            EmoteTabView(
                tab = tab,
                selected = tab == selectedTab,
                onClick = {
                    onTabSelected(it)
                }
            )
        }
    }
}

@Composable
private fun EmoteTabView(
    modifier: Modifier = Modifier,
    tab: EmoteTab,
    selected: Boolean,
    onClick: (EmoteTab) -> Unit,
) {
    val imageBackground =
        if (selected) RumbleCustomTheme.colors.backgroundHighlight else RumbleCustomTheme.colors.subtleHighlight

    Box(
        modifier = modifier
            .height(emojiTabHeight)
            .width(emojiTabWidth)
            .clip(RoundedCornerShape(radiusXXXMedium))
            .conditional(selected) {
                background(RumbleCustomTheme.colors.backgroundHighlight)
            }
            .clickable { onClick(tab) }
    ) {
        tab.pictureId?.let {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(imageSmall),
                painter = painterResource(it),
                contentDescription = tab.channelName,
                tint = RumbleCustomTheme.colors.primary,
            )
        } ?: run {
            ProfileImageComponent(
                modifier = Modifier
                    .align(Alignment.Center),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageSmallStyle(
                    borderColor = null,
                    imageBackground = if (tab.pictureUrl != null) imageBackground else null
                ),
                userName = tab.channelName ?: "",
                userPicture = tab.pictureUrl ?: "",
            )
        }
    }
}
