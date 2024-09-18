package com.rumble.ui3.subscriptions.pages.list

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed

object AllSubscriptionState {
    var subscriptionsVideosPagingDataMap: PagingData<Feed>? = null
    var lastFocusedView: LastFocusedView = LastFocusedView.NONE

    enum class LastFocusedView{
        REFRESH_BUTTON,
        CHANNEL_RECOMMENDATION,
        DATA_GRID,
        NONE
    }
}