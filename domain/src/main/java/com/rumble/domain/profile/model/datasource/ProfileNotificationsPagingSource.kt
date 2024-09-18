package com.rumble.domain.profile.model.datasource

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.model.getProfileNotificationEntity
import com.rumble.domain.profile.domainmodel.ProfileNotificationEntity
import com.rumble.network.api.UserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ProfileNotificationsPagingSource(
    private val userApi: UserApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, ProfileNotificationEntity>() {

    private var nextKey = 0
    private var offset = 0

    override fun getRefreshKey(state: PagingState<Int, ProfileNotificationEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProfileNotificationEntity> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                nextKey = params.key ?: 0
                val items = fetchProfileNotificationsList(loadSize)

                LoadResult.Page(
                    data = items,
                    prevKey = null,
                    nextKey = if (items.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true

    private suspend fun fetchProfileNotificationsList(loadSize: Int): List<ProfileNotificationEntity> {
        val response =
            userApi.fetchProfileNotifications(offset, loadSize).body()
        val items = response?.data?.notificationsList?.map { it.getProfileNotificationEntity() }
            ?: emptyList()
        offset += loadSize
        return items
    }
}