package com.rumble.utils

import androidx.compose.ui.text.SpanStyle
import java.util.regex.Pattern

data class RumbleUrlAnnotation(
    val url: String,
    val start: Int,
    val end: Int,
    val spanStyle: SpanStyle? = null
)

fun getRumbleUrlAnnotations(text: String, spanStyle: SpanStyle? = null): List<RumbleUrlAnnotation> {
    val urlPattern: Pattern =
        Pattern.compile(RumbleConstants.URL_PATTERN_REGEX, Pattern.MULTILINE)
    val matcher = urlPattern.matcher(text)
    var matchStart: Int
    var matchEnd: Int
    val annotations = arrayListOf<RumbleUrlAnnotation>()

    while (matcher.find()) {
        matchStart = matcher.start(1)
        matchEnd = matcher.end()

        var url = text.substring(matchStart, matchEnd)
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "https://$url"

        annotations.add(RumbleUrlAnnotation(url, matchStart, matchEnd, spanStyle))
    }
    return annotations
}