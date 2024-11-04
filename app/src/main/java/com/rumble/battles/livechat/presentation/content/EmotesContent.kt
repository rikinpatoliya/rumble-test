package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.battles.commonViews.GifImage
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.theme.liveChatEmoteSize
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.getEmoteName

@Composable
fun emotesContent(emotes: List<String>, liveChatConfig: LiveChatConfig?) =
    emotes.associateWith { id ->
        val emoteName = id.getEmoteName()
        InlineTextContent(
            Placeholder(
                width = liveChatEmoteSize,
                height = liveChatEmoteSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                liveChatConfig?.emoteList?.find { emote -> emote.name == emoteName }?.url?.let { imageUrl ->
                    if (imageUrl.contains(RumbleConstants.GIF_SUFFIX)) {
                        GifImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(alignment = Alignment.CenterEnd),
                            imageUrl = imageUrl
                        )
                    } else {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .align(alignment = Alignment.CenterEnd),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(liveChatConfig.emoteList?.find { emote -> emote.name == emoteName }?.url)
                                .build(),
                            contentDescription = "",
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
