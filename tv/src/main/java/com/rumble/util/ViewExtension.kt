package com.rumble.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.fragment.app.FragmentManager
import com.rumble.ui3.main.RumbleAlertDialogFragment
import kotlin.math.roundToInt

/** Set the View visibility to VISIBLE and eventually animate the View alpha till 100% */
fun View.visible(animate: Boolean = true, duration: Long = 100) {
    if (animate) {
        animate().alpha(1f).setDuration(duration).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                visibility = View.VISIBLE
            }
        })
    } else {
        visibility = View.VISIBLE
    }
}

/** Set the View visibility to INVISIBLE and eventually animate view alpha till 0% */
fun View.invisible(animate: Boolean = true) {
    hide(View.INVISIBLE, animate)
}

/** Set the View visibility to GONE and eventually animate view alpha till 0% */
fun View.gone(animate: Boolean = true) {
    hide(View.GONE, animate)
}

/** Convenient method that chooses between View.visible() or View.invisible() methods */
fun View.visibleOrInvisible(show: Boolean, animate: Boolean = true) {
    if (show) visible(animate) else invisible(animate)
}

/** Convenient method that chooses between View.visible() or View.gone() methods */
fun View.visibleOrGone(show: Boolean, animate: Boolean = true) {
    if (show) visible(animate) else gone(animate)
}

fun View.hide(hidingStrategy: Int, animate: Boolean = true) {
    if (animate) {
        animate().alpha(0f).setDuration(200).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                visibility = hidingStrategy
            }
        })
    } else {
        visibility = hidingStrategy
    }
}

fun FragmentManager.showAlert(message: String, showIcon: Boolean) {
    RumbleAlertDialogFragment(message = message, showIcon).show(
        this,
        RumbleAlertDialogFragment::class.java.simpleName
    )
}

val Context.isNetworkConnected: Boolean
    get() {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            manager.getNetworkCapabilities(manager.activeNetwork)?.let {
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
            } ?: false
        else
            @Suppress("DEPRECATION")
            manager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
}