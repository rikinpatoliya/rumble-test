package com.rumble.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent

object LinkUtil {

    fun extractLinks(text: String): List<LinkUrl> {
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

    fun openInAppBrowser(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        try {
            val link = if (!url.startsWith("http")) {
                "https://$url"
            } else {
                url
            }
            customTabsIntent.launchUrl(context, Uri.parse(link))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No browser found", Toast.LENGTH_LONG).show()
        }
    }

    data class LinkUrl(
        val url: String,
        val start: Int,
        val end: Int
    )
}