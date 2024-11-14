package com.rumble.domain.common.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.rumble.network.di.IoDispatcher
import com.rumble.network.session.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CleanupService: Service() {
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private lateinit var scope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(ioDispatcher)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        scope.launch { sessionManager.allowContentLoadFlow(true) }
        stopSelf()
    }
}