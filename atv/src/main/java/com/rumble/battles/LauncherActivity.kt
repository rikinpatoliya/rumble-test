package com.rumble.battles

import android.os.Bundle
import com.rumble.MainActivityNew

class LauncherActivity : MainActivityNew(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            // This is due to the app's unexpected behaviour of recreating the activity after reopening it from the background.
            finish()
            return
        }
    }
}