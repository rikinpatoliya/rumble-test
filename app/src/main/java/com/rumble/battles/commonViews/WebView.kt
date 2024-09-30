package com.rumble.battles.commonViews

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RumbleWebView(url: String) {

    AndroidView(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        factory = {
            WebView(it).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        })
}