package com.rumble.domain.sort

import com.rumble.domain.R
import com.rumble.network.queryHelpers.Date
import com.rumble.network.queryHelpers.Duration
import com.rumble.network.queryHelpers.Frequency
import com.rumble.network.queryHelpers.Sort

sealed interface SortFilter {
    val nameId: Int
}

enum class SortType(
    override val nameId: Int,
    val sortQuery: Sort?,
) : SortFilter {
    RELEVANCE(R.string.sort_relevance, null),
    MOST_RECENT(R.string.sort_most_recent, Sort.DATE),
    LIKES(R.string.sort_likes, Sort.RUMBLES),
    VIEWS(R.string.sort_views, Sort.VIEWS)
}

enum class SortFollowingType(
    override val nameId: Int,
) : SortFilter {
    DEFAULT(R.string.default_sort),
    NAME_A_Z(R.string.name_a_z),
    NAME_Z_A(R.string.name_z_a),
    FOLLOWERS_HIGHEST(R.string.followers_highest),
    FOLLOWERS_LOWEST(R.string.followers_lowest)
}

enum class CommentSortOrder(
    override val nameId: Int,
) : SortFilter {
    NEW(R.string.sort_new),
    POPULAR(R.string.sort_popular)
}

// TODO give this a better name
enum class FilterType(
    override val nameId: Int,
    val dateFilter: Date?,
) : SortFilter {
    ANY_TIME(R.string.any_time, null),
    TODAY(R.string.today, Date.TODAY),
    THIS_WEEK(R.string.this_week, Date.THIS_WEEK),
    THIS_MONTH(R.string.this_month, Date.THIS_MONTH),
    THIS_YEAR(R.string.this_year, Date.THIS_YEAR),
}

enum class DurationType(
    override val nameId: Int,
    val duration: Duration?,
) : SortFilter {
    ANY(R.string.any_duration, null),
    SHORT(R.string.short_duration, Duration.SHORT),
    LONG(R.string.long_duration, Duration.LONG),
}

data class NotificationFrequency(
    override val nameId: Int,
    val frequency: Frequency
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