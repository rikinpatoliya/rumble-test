package com.rumble.ui3.subscriptions

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity

sealed class SubscriptionsFragmentStates {

    object Loading : SubscriptionsFragmentStates()
    object Error : SubscriptionsFragmentStates()
    object NotLoggedIn : SubscriptionsFragmentStates()
    object NoSubscriptions : SubscriptionsFragmentStates()
    data class SubscriptionsList(
        val sortedList: List<CreatorEntity>,
        val originalList: List<CreatorEntity>,
    ) : SubscriptionsFragmentStates()

}
