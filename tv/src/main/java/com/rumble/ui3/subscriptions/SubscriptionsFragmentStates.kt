package com.rumble.ui3.subscriptions

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity

sealed class SubscriptionsFragmentStates {

    object Loading : SubscriptionsFragmentStates()
    object Error : SubscriptionsFragmentStates()
    object NotLoggedIn : SubscriptionsFragmentStates()
    object NoSubscriptions : SubscriptionsFragmentStates()
    data class SubscriptionsList(
        val sortedList: List<ChannelDetailsEntity>,
        val originalList: List<ChannelDetailsEntity>,
    ) : SubscriptionsFragmentStates()

}
