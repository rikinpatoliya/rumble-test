package com.rumble.battles.premium.presentation

import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionData

interface PremiumSubscriptionHandler {
    val subscriptionList: List<PremiumSubscriptionData>

    fun onShowPremiumPromo()
    fun onClosePremiumPromo()
    fun onGetPremium()
    fun onShowSubscriptionOptions()
    fun onRestoreSubscription()
    fun onLinkClicked(link: String)
    fun onSubscribe(premiumSubscriptionData: PremiumSubscriptionData)
}