package com.rumble.domain.common.domain.usecase

import android.util.Patterns
import javax.inject.Inject


class ExtractLinksUseCase @Inject constructor() {

    operator fun invoke(text: String): List<LinkUrl> {
        val matcher = Patterns.WEB_URL.matcher(text)
        var matchStart: Int
        var matchEnd: Int
        val links = arrayListOf<LinkUrl>()

        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()
            val url = text.substring(matchStart, matchEnd)
            links.add(LinkUrl(url, matchStart, matchEnd))
        }
        return links
    }
}

data class LinkUrl(
    val url: String,
    val start: Int,
    val end: Int
)