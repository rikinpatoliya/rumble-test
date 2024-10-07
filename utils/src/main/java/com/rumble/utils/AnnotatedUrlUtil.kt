package com.rumble.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
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
        if (!url.startsWith(RumbleConstants.HTTP_PREFIX) && !url.startsWith(RumbleConstants.HTTPS_PREFIX))
            url = "${RumbleConstants.HTTPS_PREFIX}$url"

        annotations.add(RumbleUrlAnnotation(url, matchStart, matchEnd, spanStyle))
    }
    return annotations
}

fun getUrlAnnotatedString(annotatedString: AnnotatedString, linkColor: Color): AnnotatedString {
    val rumbleUrlAnnotations = getRumbleUrlAnnotations(annotatedString.text)
    return buildAnnotatedString {
        append(annotatedString)
        rumbleUrlAnnotations.forEach {
            addStyle(
                style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                start = it.start,
                end = it.end
            )
            addStringAnnotation(
                tag = RumbleConstants.TAG_URL,
                annotation = it.url,
                start = it.start,
                end = it.end
            )
        }
    }
}