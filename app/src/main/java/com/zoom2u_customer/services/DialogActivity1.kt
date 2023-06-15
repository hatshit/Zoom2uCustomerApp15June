package com.zoom2u_customer.services


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.zoom2u_customer.R

class DialogActivity1 : Activity() {
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        showNotificationDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showNotificationDialog()
    }


    private fun showNotificationDialog() {
        try {
            if (MyFcmListenerService.defaultNotificDialog != null)
                if (MyFcmListenerService.defaultNotificDialog?.isShowing == true)
                    MyFcmListenerService.defaultNotificDialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (MyFcmListenerService.defaultNotificDialog != null)
                MyFcmListenerService.defaultNotificDialog = null
            MyFcmListenerService.defaultNotificDialog =
                Dialog(this@DialogActivity1, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
            MyFcmListenerService.defaultNotificDialog?.setCancelable(false)
            MyFcmListenerService.defaultNotificDialog?.window?.setBackgroundDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                )
            )
            MyFcmListenerService.defaultNotificDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            MyFcmListenerService.defaultNotificDialog?.setContentView(R.layout.default_notificationview1)
            val window: Window? = MyFcmListenerService.defaultNotificDialog?.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val wlp = window?.attributes
            wlp?.gravity = Gravity.TOP
            wlp?.flags = wlp?.flags?.and(WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv())
            window?.attributes = wlp
            val titleDialogDefaultNoti =
                MyFcmListenerService.defaultNotificDialog?.findViewById(R.id.titleDialogDefaultNoti) as TextView
            titleDialogDefaultNoti.text = "Booking Status!"

            val dialogMessageTextDefaultNoti =
                MyFcmListenerService.defaultNotificDialog?.findViewById(R.id.dialogMessageTextDefaultNoti) as TextView

            dialogMessageTextDefaultNoti.setText(MyFcmListenerService.notificationMessage)
            val closeDefaultNoti =
                MyFcmListenerService.defaultNotificDialog?.findViewById(R.id.closeDefaultNoti) as ImageView
            closeDefaultNoti.setOnClickListener {
                MyFcmListenerService.defaultNotificDialog?.dismiss()
                MyFcmListenerService.notificationMessage = ""
                finish()
            }
            MyFcmListenerService.defaultNotificDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
            MyFcmListenerService.notificationMessage = ""
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        MyFcmListenerService.notificationMessage = ""
    }
}