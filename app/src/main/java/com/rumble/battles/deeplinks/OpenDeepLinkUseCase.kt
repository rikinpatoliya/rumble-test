package com.rumble.battles.deeplinks

import androidx.media3.common.util.UnstableApi
import com.appsflyer.deeplink.DeepLink
import com.rumble.domain.notifications.model.NotificationDataManager
import com.rumble.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val NITROCROSS_CAMPAIGN = "Nitrocross"
private const val NITROCROSS_CHANNEL_ID = "_c2968864"

@UnstableApi
class OpenDeepLinkUseCase @Inject constructor(
    private val notificationDataManager: NotificationDataManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    private val scope: CoroutineScope = CoroutineScope(ioDispatcher)

    operator fun invoke(deepLink: DeepLink) {
        if (deepLink.campaign.equals(NITROCROSS_CAMPAIGN, ignoreCase = true)
        ) {
            scope.launch {
                notificationDataManager.saveDeepLinkChannelId(NITROCROSS_CHANNEL_ID)
            }
        }
    }
}