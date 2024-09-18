package com.rumble.utils.extension

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display
import android.view.WindowManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            continuation.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }
}

fun Context.isScreenOn(): Boolean {
    val dm = getSystemService(FragmentActivity.DISPLAY_SERVICE) as DisplayManager
    var screenOn = false
    for (display in dm.displays) {
        if (display.state != Display.STATE_OFF) {
            screenOn = true
        }
    }
    return screenOn
}

fun Context.keepScreenOn(keepOn: Boolean) {
    if (keepOn)
        (this as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    else
        (this as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
