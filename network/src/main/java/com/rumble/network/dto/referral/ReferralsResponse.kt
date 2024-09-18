package com.rumble.network.dto.referral

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class ReferralsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val referralsData: ReferralsData
)
