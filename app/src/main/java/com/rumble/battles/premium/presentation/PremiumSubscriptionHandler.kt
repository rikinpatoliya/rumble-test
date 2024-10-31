package com.rumble.battles.premium.presentation

import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionData
import com.rumble.network.queryHelpers.SubscriptionSource

interface PremiumSubscriptionHandler {
    val subscriptionList: List<PremiumSubscriptionData>

    fun onShowPremiumPromo(videoId: Long?, source: SubscriptionSource?)
    fun onClosePremiumPromo()
    fun onGetPremium()
    fun onShowSubscriptionOptions(videoId: Long? = null, creatorId: String? = null, source: SubscriptionSource?)
    fun onSubscribe(premiumSubscriptionData: PremiumSubscriptionData)
}