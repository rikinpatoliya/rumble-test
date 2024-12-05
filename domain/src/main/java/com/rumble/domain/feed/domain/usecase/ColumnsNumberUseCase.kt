package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.common.domain.usecase.GetScreenWidthUseCase
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.utils.RumbleConstants.HOME_SCREEN_ROWS_1
import com.rumble.utils.RumbleConstants.HOME_SCREEN_ROWS_2
import com.rumble.utils.RumbleConstants.HOME_SCREEN_ROWS_3
import com.rumble.utils.RumbleConstants.SCREEN_WIDTH_THRESHOLD_1000
import com.rumble.utils.RumbleConstants.SCREEN_WIDTH_THRESHOLD_600
import javax.inject.Inject

class ColumnsNumberUseCase @Inject constructor(
    private val getScreenWidthUseCase: GetScreenWidthUseCase
) {

    operator fun invoke(type: VideoCollectionType?): Int {
        val screenWidth = getScreenWidthUseCase()
        return when {
            type == VideoCollectionType.MyFeed || type == VideoCollectionType.Reposts || type == null -> HOME_SCREEN_ROWS_1
            screenWidth < SCREEN_WIDTH_THRESHOLD_600 -> HOME_SCREEN_ROWS_1
            screenWidth in SCREEN_WIDTH_THRESHOLD_600..SCREEN_WIDTH_THRESHOLD_1000 -> HOME_SCREEN_ROWS_2
            else -> HOME_SCREEN_ROWS_3
        }
    }
}