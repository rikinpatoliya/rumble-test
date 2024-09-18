package com.rumble.ui3.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.rumble.R
import com.rumble.utils.RumbleConstants.TV_ALERT_DIALOG_DISPLAY_TIME

class RumbleAlertDialogFragment(
    val message: String,
    val showIcon: Boolean = true,
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.InternetConnectionLostDialogStyle)
        val inflater = layoutInflater
        val customView = inflater.inflate(R.layout.dialog_alert, null)

        val textView = customView.findViewById<TextView>(R.id.message_text)
        textView.text = message

        val iconView = customView.findViewById<View>(R.id.alert_icon)
        iconView.visibility = if (showIcon) {
            View.VISIBLE
        } else {
            View.GONE
        }

        customView.requestLayout()

        alertDialogBuilder.setView(customView)
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setCanceledOnTouchOutside(true)

        alertDialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val layoutParams = alertDialog.window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.END
        alertDialog.window?.attributes = layoutParams

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog?.isShowing == true) {
                dismiss()
            }
        }, TV_ALERT_DIALOG_DISPLAY_TIME)

        return alertDialog
    }
}