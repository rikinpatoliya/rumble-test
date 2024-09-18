package com.rumble.domain.discover.domain.domainmodel

import androidx.annotation.StringRes
import com.rumble.domain.R
import java.lang.IllegalArgumentException

enum class CategoryDisplayType(
    @StringRes val label: Int,
    val index: Int,
    val mainIndex: Int,
    val analyticName: String
) {
    LIVE_STREAM(R.string.category_live_streams, 0, 1, "live"),
    RECORDED_STREAM(R.string.category_recorded_streams, 1, -1, "streamed"),
    VIDEOS(R.string.category_videos, 2, -1, "regular"),
    CATEGORIES(R.string.category_categories, 3, 0, "categories");

    companion object {
        fun getDisplayTypeList(isPrimary: Boolean): List<CategoryDisplayType> {
            return if (isPrimary) values().toList()
            else listOf(LIVE_STREAM, RECORDED_STREAM, VIDEOS)
        }

        fun getMainCategoryTypeList(): List<CategoryDisplayType> =
            listOf(CATEGORIES, LIVE_STREAM)

        fun getByName(typeName: String): CategoryDisplayType? {
            return try {
                CategoryDisplayType.valueOf(typeName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}