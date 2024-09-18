package com.rumble.domain.common.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.domainmodel.DeviceType
import com.rumble.utils.RumbleConstants.PAGINATION_VIDEO_PAGE_SIZE
import com.rumble.utils.RumbleConstants.PAGINATION_VIDEO_PAGE_SIZE_TABLET_TV
import javax.inject.Inject

class GetVideoPageSizeUseCase @Inject constructor(
    private val deviceType: DeviceType,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    operator fun invoke(): Int =
        if (deviceType == DeviceType.Phone) {
            PAGINATION_VIDEO_PAGE_SIZE
        } else {
            PAGINATION_VIDEO_PAGE_SIZE_TABLET_TV
        }
}