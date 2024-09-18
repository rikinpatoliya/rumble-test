package com.rumble.utils.extension

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.findFirstFullyVisibleItemIndex(
    indexShift: Int = 0,
    visibilityPercentage: Float = 1f
): Int {
    layoutInfo.visibleItemsInfo
        .forEach { itemInfo ->
            val itemStartOffset = itemInfo.offset
            val itemEndOffset = itemInfo.offset + itemInfo.size
            val viewportStartOffset = layoutInfo.viewportStartOffset
            val viewportEndOffset = layoutInfo.viewportEndOffset
            if (itemStartOffset >= viewportStartOffset && itemEndOffset * visibilityPercentage <= viewportEndOffset) {
                return itemInfo.index - indexShift
            }
        }
    return -1
}