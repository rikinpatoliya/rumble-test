package com.rumble.domain.livechat.domain.domainmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiveGateEntity(
    val videoTimeCode: Long,
    val countDownValue: Int,
) : Parcelable