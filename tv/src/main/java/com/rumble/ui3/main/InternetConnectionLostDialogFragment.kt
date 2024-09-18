package com.rumble.ui3.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.rumble.R

class InternetConnectionLostDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(requireContext(), R.style.InternetConnectionLostDialogStyle)
        val inflater = layoutInflater
        val customView = inflater.inflate(R.layout.dialog_alert, null)
        val textView = customView.findViewById<TextView>(R.id.message_text)
        textView.text = getString(R.string.internet_connection_lost)

        alertDialogBuilder.setView(customView)
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.setCanceledOnTouchOutside(false)

        alertDialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        val layoutParams = alertDialog.window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM or Gravity.END
        alertDialog.window?.attributes = layoutParams

        return alertDialog
    }
}