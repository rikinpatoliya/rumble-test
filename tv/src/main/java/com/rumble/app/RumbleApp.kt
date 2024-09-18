package com.rumble.app

import android.app.Application
import android.os.StrictMode
import androidx.databinding.DataBindingUtil
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rumble.BuildConfig
import com.rumble.di.BindingComponentBuilder
import com.rumble.di.DataBindingEntryPoint
import com.rumble.util.CrashReportingTree
import dagger.hilt.EntryPoints
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject
import javax.inject.Provider


open class RumbleApp : Application(), Configuration.Provider {

    @Inject
    lateinit var appManager: RumbleAppManager

    @Inject
    lateinit var bindingComponentProvider: Provider<BindingComponentBuilder>

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Enable DI for data binding
        val dataBindingComponent = bindingComponentProvider.get().build()
        val dataBindingEntryPoint = EntryPoints.get(dataBindingComponent, DataBindingEntryPoint::class.java)
        DataBindingUtil.setDefaultComponent(dataBindingEntryPoint)

        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        appManager.onAppLaunch()
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )
    }
}