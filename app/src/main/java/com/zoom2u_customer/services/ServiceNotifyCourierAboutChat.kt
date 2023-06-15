package com.zoom2u_customer.services

import android.app.IntentService
import android.content.Intent
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi


class ServiceNotifyCourierAboutChat : IntentService("Service notify courier about chat") {
    override fun onHandleIntent(intent: Intent?) {
        try {
            val repository: ServiceNotifyRepository
            val serviceApi: ServiceApi = ApiClient.getServices()
            repository = ServiceNotifyRepository(serviceApi)
            val courierId = intent?.getStringExtra("CourierId")
            val msgStr = intent?.getStringExtra("Message")
            repository.serviceNotify(courierId,msgStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}