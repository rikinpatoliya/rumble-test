package com.rumble.ui3.live

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import java.util.Date

object LiveStates {
    var reloadLiveData: Boolean = false
    var liveVideosPagingDataMap: PagingData<Feed>? = null
}