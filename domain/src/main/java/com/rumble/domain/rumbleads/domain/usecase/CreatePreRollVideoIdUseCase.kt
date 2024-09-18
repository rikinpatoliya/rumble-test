package com.rumble.domain.rumbleads.domain.usecase

import javax.inject.Inject

class CreatePreRollVideoIdUseCase @Inject constructor() {
    operator fun invoke(videoId: Long) = "v${videoId.toString(36)}"
}