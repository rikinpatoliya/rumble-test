package com.rumble.domain.channels.channeldetails.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

sealed class UserUploadChannelsResult {

    data class UserUploadChannelsSuccess(val userUploadChannels: List<UserUploadChannelEntity>) :
        UserUploadChannelsResult()

    data class UserUploadChannelsError(override val rumbleError: RumbleError?) :
        UserUploadChannelsResult(),
        RumbleResult

}