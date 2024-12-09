package com.rumble.domain.channels.channeldetails.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class FetchChannelDataResult {
    data class Success(val channelData: CreatorEntity) : FetchChannelDataResult()
    data class Failure(val rumbleError: RumbleError, val errorMessage: String?) :
        FetchChannelDataResult()
}