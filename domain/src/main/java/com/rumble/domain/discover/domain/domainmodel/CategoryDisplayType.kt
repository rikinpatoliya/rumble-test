package com.rumble.domain.discover.domain.domainmodel

import androidx.annotation.StringRes
import com.rumble.domain.R

enum class CategoryDisplayType(
    @StringRes val label: Int,
    val index: Int,
    val mainIndex: Int,
    val channelDetailsIndex: Int,
    val analyticName: String
) {
    LIVE_STREAM(R.string.category_live_streams, 0, 1, -1, "live"),
    RECORDED_STREAM(R.string.category_recorded_streams, 1, -1, -1,"streamed"),
    VIDEOS(R.string.category_videos, 2, -1, 0,"regular"),
    CATEGORIES(R.string.category_categories, 3, 0, -1,"categories"),
    REPOSTS(R.string.category_reposts, 4, -1,1, "reposts");

    companion object {
        fun getDisplayTypeList(hasSubcategories: Boolean): List<CategoryDisplayType> {
            return if (hasSubcategories) listOf(LIVE_STREAM, RECORDED_STREAM, VIDEOS, CATEGORIES)
            else listOf(LIVE_STREAM, RECORDED_STREAM, VIDEOS)
        }

        fun getMainCategoryTypeList(): List<CategoryDisplayType> =
            listOf(CATEGORIES, LIVE_STREAM)

        fun getChannelDetailsCategoryTypeList(): List<CategoryDisplayType> =
            listOf(VIDEOS, REPOSTS)

        fun getByName(typeName: String): CategoryDisplayType? {
            return try {
                CategoryDisplayType.valueOf(typeName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}