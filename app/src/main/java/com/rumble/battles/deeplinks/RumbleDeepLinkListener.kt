package com.rumble.battles.deeplinks

import androidx.media3.common.util.UnstableApi
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import javax.inject.Inject


private const val TAG = "RumbleDeepLinkListener"

@UnstableApi
class RumbleDeepLinkListener @Inject constructor(
    private val openDeepLinkUseCase: OpenDeepLinkUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : DeepLinkListener {

    override fun onDeepLinking(deepLinkResult: DeepLinkResult) {
        when (deepLinkResult.status) {
            DeepLinkResult.Status.FOUND -> {
                deepLinkResult.deepLink?.let {
                    openDeepLinkUseCase(it)
                }
                return
            }

            DeepLinkResult.Status.NOT_FOUND -> {
                //No need to do anything
                return
            }

            DeepLinkResult.Status.ERROR -> {
                val error: DeepLinkResult.Error = deepLinkResult.error
                unhandledErrorUseCase(TAG, Throwable("DeepLinkResult.Error -> $error"))
                return
            }
        }
    }
}