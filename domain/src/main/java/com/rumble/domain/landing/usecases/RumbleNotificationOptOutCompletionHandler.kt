package com.rumble.domain.landing.usecases

import com.onesignal.OneSignal
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "RumbleNotificationOptOutCompletionHandler"
private const val BACKUP_DELAY_ONESIGNAL_COMPLETION_HANDLER = 15000L

class RumbleNotificationOptOutCompletionHandler @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    @IoDispatcher private val backgroundDispatcher: CoroutineDispatcher
) : OneSignal.OSExternalUserIdUpdateCompletionHandler {

    private var continuation: Continuation<Unit>? = null

    suspend operator fun invoke() = suspendCoroutine { continuation ->
        this.continuation = continuation
        CoroutineScope(backgroundDispatcher).launch {
            delay(BACKUP_DELAY_ONESIGNAL_COMPLETION_HANDLER)
            try {
                continuation.resume(Unit)
            } catch (e: IllegalStateException) {
                Timber.d("$TAG - $e")
            }
        }

        CoroutineScope(backgroundDispatcher).launch {
            OneSignal.removeExternalUserId(this@RumbleNotificationOptOutCompletionHandler)
        }
    }

    override fun onSuccess(p0: JSONObject?) {
        try {
            continuation?.resume(Unit)
        } catch (e: IllegalStateException) {
            Timber.d("$TAG - $e")
        }

    }

    override fun onFailure(error: OneSignal.ExternalIdError?) {
        unhandledErrorUseCase(TAG, Throwable(error.toString()))
        try {
            continuation?.resume(Unit)
        } catch (e: IllegalStateException) {
            Timber.d("$TAG - $e")
        }
    }
}