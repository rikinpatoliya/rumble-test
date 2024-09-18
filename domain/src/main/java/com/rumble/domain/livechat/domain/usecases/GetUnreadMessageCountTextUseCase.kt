package com.rumble.domain.livechat.domain.usecases

import com.rumble.utils.RumbleConstants.MAX_UNREAD_MESSAGE
import javax.inject.Inject

class GetUnreadMessageCountTextUseCase @Inject constructor() {
    operator fun invoke(count: Int): String =
        if (count <= MAX_UNREAD_MESSAGE) count.toString() else ("$MAX_UNREAD_MESSAGE+")
}