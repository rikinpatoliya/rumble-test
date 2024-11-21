package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import javax.inject.Inject

class CreateKeywordsUseCase @Inject constructor() {
    private val regex = Regex("[^A-Za-z0-9]")
    private val delimiter = " "
    private val separator = ","
    private val minLength = 2

    operator fun invoke(input: List<Feed?>): String =
        input
            .asSequence()
            .filterNotNull()
            .filterIsInstance<VideoEntity>()
            .flatMap { videoEntity -> listOf(videoEntity.title, videoEntity.channelName) }
            .map { it.replace(regex, delimiter) }
            .flatMap { it.split(delimiter) }
            .filter { it != delimiter && it.count() > minLength }
            .toSet()
            .joinToString(separator)
}