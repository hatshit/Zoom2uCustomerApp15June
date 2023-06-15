package com.zoom2u_customer.utility

import android.app.Application
import android.util.Log
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.firebase.FirebaseApp
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.zoom2u_customer.apiclient.ApiClient


class Zoom2u : Application(), OnMapsSdkInitializedCallback {

    //private val appCenterKey="7af9dd8b-f9b2-4c28-909d-99faeafd4cbb" //mahendra sir
    //private val appCenterKey="c1415913-68da-4159-98a4-33f49149c349" //jyotiraditya
    private var appCenterKey:String?=null
    companion object {
        private var mInstance: Zoom2u? = null
        fun getInstance(): Zoom2u? {
            return mInstance
        }

    }

    override fun onCreate() {
        super.onCreate()
        appCenterKey = if(ApiClient.BaseUrl=="https://api.zoom2u.com/")
            "7af9dd8b-f9b2-4c28-909d-99faeafd4cbb" //mahendra sir
        else
            "c1415913-68da-4159-98a4-33f49149c349" //jyotiraditya

        FirebaseApp.initializeApp(this)
        AppCenter.start(this,appCenterKey,
            Analytics::class.java, Crashes::class.java)
        mInstance = this
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Log.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Log.d("MapsDemo", "The legacy version of the renderer is used.")
        }

    }

}