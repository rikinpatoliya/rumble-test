package com.rumble.util

import android.app.Activity
import java.util.Stack

object ActivityStackManager {
    private val activityStack = Stack<Activity>()
    fun addActivity(activity: Activity) {
        activityStack.push(activity)
    }

    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    fun isActivityInStack(activityClass: Class<out Activity?>): Boolean {
        for (activity in activityStack) {
            if (activity.javaClass == activityClass) {
                return true
            }
        }
        return false
    }

    fun clearStack() {
        activityStack.clear()
    }
}