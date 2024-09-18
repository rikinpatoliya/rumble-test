package com.rumble.utils.extension

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlin.system.exitProcess

fun Context.restartApp(launchActivity: Intent) {
    val mPendingIntentId = 1234567
    val pendingIntent = PendingIntent.getActivity(
        this.applicationContext, mPendingIntentId, launchActivity,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_CANCEL_CURRENT
    )
    val alarmManager: AlarmManager =
        this.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
    exitProcess(0)
}
