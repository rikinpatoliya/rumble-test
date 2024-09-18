package com.rumble.domain.feed.domain.usecase

import javax.inject.Inject

class ValidCommentUseCase @Inject constructor() {
    operator fun invoke(comment: String): Boolean = comment.trim().length > 2
}