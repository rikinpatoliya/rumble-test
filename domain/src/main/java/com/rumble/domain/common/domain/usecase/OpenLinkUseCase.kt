package com.rumble.domain.common.domain.usecase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "OpenLinkUseCase"

class OpenLinkUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val openUriUseCase: OpenUriUseCase,
) {
    operator fun invoke(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val link = if (!url.startsWith("http")) {
            "https://$url"
        } else {
            url
        }
        try {
            customTabsIntent.launchUrl(context, Uri.parse(link))
        } catch (e: ActivityNotFoundException) {
            openUriUseCase.invoke(TAG, link)
        }
    }
}