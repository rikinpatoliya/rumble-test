package com.rumble.network.dto.referral

import com.google.gson.annotations.SerializedName

data class ReferralsData(
    @SerializedName("referrals")
    val referrals: List<Referral>,
    @SerializedName("ticket_total")
    val ticketTotal: Int,
    @SerializedName("tickets")
    val tickets: Ticket,
    @SerializedName("referral_num_impressions")
    val impressions: Int,
    @SerializedName("commission_total")
    val commissionTotal: Double
)
