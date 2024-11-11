package com.rumble.battles.livechat.presentation.emoji

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.imageXMedium
import com.rumble.theme.paddingSmall
import com.rumble.utils.RumbleConstants.EMOTES_ROW_NUMBER
import kotlin.math.ceil

@Composable
fun EmoteGroupView(
    modifier: Modifier = Modifier,
    title: String = "",
    emoteList: List<EmoteEntity> = emptyList(),
    onSelectEmote: (EmoteEntity) -> Unit = {},
) {
    Column(modifier.fillMaxSize()) {
        Text(
            modifier = Modifier.padding(bottom = paddingSmall),
            text = title,
            color = RumbleCustomTheme.colors.primary,
            style = h6,
        )

        LazyHorizontalGrid(
            modifier = Modifier.width(calculateGridWidth(emoteList.size)),
            rows = GridCells.Fixed(EMOTES_ROW_NUMBER),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            verticalArrangement = Arrangement.spacedBy(paddingSmall),
        ) {
            items(emoteList) {
                EmoteImageView(
                    modifier = Modifier.wrapContentSize(),
                    emoteSize = imageXMedium,
                    emoteEntity = it,
                    onClick = onSelectEmote,
                )
            }
        }
    }
}

private fun calculateGridWidth(emoteCount: Int): Dp {
    val columns = ceil(emoteCount / EMOTES_ROW_NUMBER.toDouble()).toInt()
    return (columns * imageXMedium.value + (columns - 1) * paddingSmall.value).dp
}
