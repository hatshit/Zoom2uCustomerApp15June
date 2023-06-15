/*
package com.zoom2u_customer.ui.notification

import android.app.IntentService
import android.content.Intent
import com.customer.servercalls.HandleServerCalls
import java.lang.Exception

class ServiceNotifyCourierAboutChat : IntentService("Service notify courier about chat") {
    override fun onHandleIntent(intent: Intent?) {
        try {
            var serviceHandlUpdateToken: HandleServerCalls? = HandleServerCalls()
            val courierId = intent!!.getStringExtra("CourierId")
            val msgStr = intent.getStringExtra("Message")
            serviceHandlUpdateToken.notifyCustomerAboutChatMessage(courierId, msgStr)
            serviceHandlUpdateToken = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}*/
