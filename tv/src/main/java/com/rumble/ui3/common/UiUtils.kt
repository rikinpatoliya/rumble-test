package com.rumble.ui3.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.AnnotatedTextAction
import com.rumble.theme.wokeGreen
import com.rumble.utils.RumbleConstants
import java.util.regex.Pattern

data class RumbleUrlAnnotation(
    val url: String,
    val start: Int,
    val end: Int,
    val spanStyle: SpanStyle = SpanStyle(
        color = wokeGreen,
        fontWeight = FontWeight.W700
    ),
)

fun parseTextWithUrls(
    text: String,
    color: Color,
    onClick: (String) -> Unit,
): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val annotationsList = mutableListOf<RumbleUrlAnnotation>()
    val urlPattern: Pattern =
        Pattern.compile(RumbleConstants.URL_PATTERN_REGEX, Pattern.MULTILINE)

    return try {
        val matcher = urlPattern.matcher(text)
        var matchStart: Int
        var matchEnd: Int

        while (matcher.find()) {
            matchStart = matcher.start(1)
            matchEnd = matcher.end()

            var url = text.substring(matchStart, matchEnd)
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "https://$url"

            actionList.add(AnnotatedTextAction(RumbleConstants.TAG_URL) { uri ->
                onClick(uri)
            })
            annotationsList.add(
                RumbleUrlAnnotation(
                    url = url,
                    start = matchStart,
                    end = matchEnd
                )
            )
        }

        val annotatedString = buildAnnotatedString {
            append(text)
            addStyle(SpanStyle(color = color), 0, text.length)
            annotationsList.forEach {
                addStringAnnotation(
                    tag = RumbleConstants.TAG_URL,
                    annotation = it.url,
                    start = it.start,
                    end = it.end
                )
                addStyle(it.spanStyle, it.start, it.end)
            }
        }
        AnnotatedStringWithActionsList(annotatedString, actionList)
    } catch (e: Exception) {
        AnnotatedStringWithActionsList(AnnotatedString(""), emptyList())
    }
}
