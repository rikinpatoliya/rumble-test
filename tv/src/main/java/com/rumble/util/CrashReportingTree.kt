package com.rumble.util

import android.util.Log
import timber.log.Timber


// TODO - implemeny real Crashlytics logging
class CrashReportingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        if (t != null) {
            if (priority == Log.ERROR) {
                // log error to crashlytics
            } else if (priority == Log.WARN) {
                // log warning to crashlytics
            }
        }
    }


}