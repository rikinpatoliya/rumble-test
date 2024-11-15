package com.rumble.domain.sort

import com.rumble.domain.R
import com.rumble.domain.SortAnyTimeTag
import com.rumble.domain.SortLikesTag
import com.rumble.domain.SortMostRecentItemTag
import com.rumble.domain.SortRelevanceItemTag
import com.rumble.domain.SortThisMonthTag
import com.rumble.domain.SortThisWeekTag
import com.rumble.domain.SortTodayTag
import com.rumble.domain.SortViewsTag
import com.rumble.network.queryHelpers.Date
import com.rumble.network.queryHelpers.Duration
import com.rumble.network.queryHelpers.Frequency
import com.rumble.network.queryHelpers.Sort

sealed interface SortFilter {
    val nameId: Int
    val testTag: String
}

enum class SortType(
    override val nameId: Int,
    val sortQuery: Sort?,
    override val testTag: String = "",
) : SortFilter {
    RELEVANCE(R.string.sort_relevance, null, SortRelevanceItemTag),
    MOST_RECENT(R.string.sort_most_recent, Sort.DATE, SortMostRecentItemTag),
    LIKES(R.string.sort_likes, Sort.RUMBLES, SortLikesTag),
    VIEWS(R.string.sort_views, Sort.VIEWS, SortViewsTag)
}

enum class SortFollowingType(
    override val nameId: Int,
    override val testTag: String = "",
) : SortFilter {
    DEFAULT(R.string.default_sort),
    NAME_A_Z(R.string.name_a_z),
    NAME_Z_A(R.string.name_z_a),
    FOLLOWERS_HIGHEST(R.string.followers_highest),
    FOLLOWERS_LOWEST(R.string.followers_lowest)
}

enum class CommentSortOrder(
    override val nameId: Int,
    override val testTag: String = "",
) : SortFilter {
    NEW(R.string.sort_new),
    POPULAR(R.string.sort_popular)
}

// TODO give this a better name
enum class FilterType(
    override val nameId: Int,
    val dateFilter: Date?,
    override val testTag: String = "",
) : SortFilter {
    ANY_TIME(R.string.any_time, null, SortAnyTimeTag),
    TODAY(R.string.today, Date.TODAY, SortTodayTag),
    THIS_WEEK(R.string.this_week, Date.THIS_WEEK, SortThisWeekTag),
    THIS_MONTH(R.string.this_month, Date.THIS_MONTH, SortThisMonthTag),
    THIS_YEAR(R.string.this_year, Date.THIS_YEAR),
}

enum class DurationType(
    override val nameId: Int,
    val duration: Duration?,
    override val testTag: String = "",
) : SortFilter {
    ANY(R.string.any_duration, null),
    SHORT(R.string.short_duration, Duration.SHORT),
    LONG(R.string.long_duration, Duration.LONG),
}

data class NotificationFrequency(
    override val nameId: Int,
    val frequency: Frequency,
    override val testTag: String = "",
) : SortFilter {
    companion object {
        val emailFrequencyTypes: List<NotificationFrequency> =
            listOf(
                NotificationFrequency(R.string.instant, Frequency.INSTANT),
                NotificationFrequency(R.string.daily, Frequency.DAILY),
                NotificationFrequency(R.string.weekly, Frequency.WEEKLY),
                NotificationFrequency(R.string.monthly, Frequency.MONTHLY),
            )
    }
}