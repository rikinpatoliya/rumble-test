package com.rumble.domain.common.model.datasource

import androidx.paging.PagingSource
import com.rumble.domain.BuildConfig
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.utils.RumbleConstants
import timber.log.Timber
import kotlin.math.min

abstract class RumblePagingSource<Key : Any, Value : Any>: PagingSource<Key, Value>() {

    private val idList: MutableList<Long> = mutableListOf()
    private val seen = mutableSetOf<Pair<Long, Feed>>()

    fun sanitizeDuplicates(items: List<VideoEntity>): List<VideoEntity> {
        val filteredItems: List<VideoEntity> = if (idList.isEmpty()) {
            items
        } else {
            items.filter {
                idList.contains(it.id).not()
            }
        }
        idList.addAll(filteredItems.map { it.id })
        return filteredItems
    }

    fun sanitizeDuplicatesById(feeds: List<Feed>): List<Feed> {
        return feeds.filter { feed ->
            val key = feed.id to feed
            seen.add(key)
        }
    }

    /**
     * The purpose of this method is to prevent the accidental fetching of more than 100 items per request as our
     * backend does not support this and the results of doing so are undefined.
     */
    fun getLoadSize(size: Int): Int {
        if (size > RumbleConstants.PAGINATION_MAX_ITEMS_PER_REQUEST) {
            // This is a guard to make absolutely sure we do not mis-configure something that results in us fetching more than 100 items.
            if (BuildConfig.DEBUG) {
                throw java.lang.RuntimeException(
                    "Rumble paging source is attempting to fetch more than $size items per request. This is not supported by the API. If you are seeing this "
                )
            }

            Timber.e(
                "Rumble paging source is attempting to fetch more than $size items per request. This is " +
                        "not supported by the API. Defaulting to ${RumbleConstants.PAGINATION_MAX_ITEMS_PER_REQUEST}"
            )
        }
        return min(size, RumbleConstants.PAGINATION_MAX_ITEMS_PER_REQUEST)
    }
}