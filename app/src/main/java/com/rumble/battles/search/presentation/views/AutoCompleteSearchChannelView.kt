package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.rumble.battles.R
import com.rumble.battles.SearchQuerySearchSuggestChannelItemTag
import com.rumble.battles.SearchQuerySearchSuggestChannelItemTitleTag
import com.rumble.battles.common.buildDelimiterHighlightedAnnotatedString
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.enforcedCloud
import com.rumble.theme.imageXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.verifiedBadgeHeightMedium
import com.rumble.utils.extension.rumbleUitTestTag

@Composable
fun AutoCompleteSearchChannelView(
    modifier: Modifier = Modifier,
    channelDetailsEntity: ChannelDetailsEntity,
    query: String,
    index: Int,
    onViewChannel: (String) -> Unit,
) {
    Box(modifier = modifier
        .rumbleUitTestTag("$SearchQuerySearchSuggestChannelItemTag$index")
        .clickable {
            onViewChannel(channelDetailsEntity.channelId)
        }
    ) {
        Row(
            modifier = modifier
                .padding(start = paddingMedium, end = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImageComponent(
                modifier = Modifier.padding(vertical = paddingXXSmall),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                userName = channelDetailsEntity.channelTitle,
                userPicture = channelDetailsEntity.thumbnail
            )
            val annotatedText = buildDelimiterHighlightedAnnotatedString(
                input = channelDetailsEntity.channelTitle,
                delimiter = query,
                regularStyle = SpanStyle(
                    fontFamily = h5.fontFamily,
                    fontWeight = h5.fontWeight,
                    fontSize = h5.fontSize,
                ),
                highlightedStyle = SpanStyle(
                    fontFamily = h5.fontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = h5.fontSize,
                ),
            )
            UserNameViewSingleLine(
                modifier = Modifier.padding(start = paddingSmall),
                name = channelDetailsEntity.channelTitle,
                nameAnnotated = annotatedText,
                verifiedBadge = channelDetailsEntity.verifiedBadge,
                verifiedBadgeHeight = verifiedBadgeHeightMedium,
                spacerWidth = paddingXXXSmall,
                textStyle = h5,
                testTag = "$SearchQuerySearchSuggestChannelItemTitleTag$index"
            )
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = R.drawable.ic_search_log),
                contentDescription = "",
                modifier = Modifier.size(imageXSmall),
                tint = enforcedCloud
            )
        }
    }
}