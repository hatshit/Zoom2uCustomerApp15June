package com.zoom2u_customer.utility


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.zoom2u_customer.R


class CustomProgressBar {

    companion object {
        var alertDialog: AlertDialog? = null

        fun progressBarShow(context: Context?, titleTxt: String?, msgTxt: String?) {
            val viewGroup = (context as Activity).findViewById<ViewGroup>(R.id.content)

            val dialogView: View =
                LayoutInflater.from(context).inflate(R.layout.custom_progressbar, viewGroup, false)

            val builder = AlertDialog.Builder(context)

            builder.setView(dialogView)
            alertDialog = builder.create()
            alertDialog?.show()
            alertDialog?.window?.setLayout(350, 350)
            alertDialog?.setCancelable(false)
            alertDialog?.setCanceledOnTouchOutside(false)
            alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val titleAlert: TextView = dialogView.findViewById(R.id.progressTitleTxt)
            titleAlert.text = titleTxt

            val msgAlertDialog: TextView = dialogView.findViewById(R.id.progressMsgTxt)
            msgAlertDialog.text = msgTxt

        }


        fun dismissProgressBar() {
            alertDialog?.dismiss()
        }
    }
}