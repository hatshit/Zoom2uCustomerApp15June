package com.zoom2u_customer.services


import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zoom2u_customer.R
import com.zoom2u_customer.ui.notification.NotificationActivity
import com.zoom2u_customer.ui.splash_screen.SplashScreenActivity



class MyFcmListenerService : FirebaseMessagingService() {
    companion object {
        var prefrenceForGCM: SharedPreferences? = null
        var loginEditorForGCM: SharedPreferences.Editor? = null
        var defaultNotificDialog: Dialog? = null
        var notificationMessage = ""

        fun notificationChannelFor8AmdLower(
            context: Context?,
            intent: PendingIntent?,
            titleStr: String?,
            message: String?,
            soundURI: Uri?,
            notificationId: Int
        ) {
            val intent = intent
            val mNotificationManager: NotificationManager = context?.getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager
            val builder: NotificationCompat.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val id = "Zoom2u Channel"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val mChannel = NotificationChannel(id, "Zoom2u", importance)
                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                mNotificationManager.createNotificationChannel(mChannel)
                builder = NotificationCompat.Builder(context, id)
                    .setContentTitle(titleStr) // required
                    .setSmallIcon(getNotificationIcon()) // required
                    .setContentText(message) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(intent)
                    .setSound(soundURI)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            } else {
                builder = NotificationCompat.Builder(context)
                    .setSound(soundURI)
                    .setContentTitle(titleStr)
                    .setContentText(message)
                    .setContentIntent(intent)
                    .setSmallIcon(getNotificationIcon())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setChannelId("Z2U Channel")
                    .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                builder.setColor(Color.parseColor("#00A6E3"))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            }
            val notification: Notification = builder.build()
            mNotificationManager.notify(notificationId, notification)

        }
        private fun getNotificationIcon(): Int {
            return R.drawable.appicon
        }
    }

    var bookingID = "0"

    var isAppPresentInBG : Boolean = true
    var notificationIntent: Intent? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }


    override fun onMessageReceived(message: RemoteMessage) {


        val data: Map<*, *> = message.data
        Log.d("MyTag",(data["message"] as String?).toString())
        if (message.notification != null) {
            notificationMessage = (data["message"] as String?).toString()
            if(data["bookingId"]==null)
                bookingID = "0"
            else
            bookingID = (data["bookingId"] as String)
            isAppOnForeground(this)
            generateNotification(this, notificationMessage, data)

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun generateNotification(
        context: MyFcmListenerService,
        message: String,
        data: Map<*, *>
    ) {
        val isRunning: Boolean = appISRunning()
        if (prefrenceForGCM == null)
                prefrenceForGCM = context.getSharedPreferences("bookingId", 0)
            if (loginEditorForGCM == null)
                loginEditorForGCM = prefrenceForGCM?.edit()
            loginEditorForGCM?.putString("bookingId", bookingID)
            loginEditorForGCM?.commit()
            if (bookingID != "0") {
                notificationMessage = ""
                if (!isAppOnForeground(this)) {
                    if (notificationIntent != null)
                        notificationIntent = null
                    notificationIntent = Intent(context, SplashScreenActivity::class.java)
                    notificationIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    notificationIntent?.action = Intent.ACTION_MAIN
                    notificationIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
                    passingNoificationIntent(notificationIntent, context, message, 0)
                } else {
                    var i: Intent? = Intent("android.intent.action.MAIN")
                    i?.setClass(context, NotificationActivity::class.java)
                    i?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(i)
                    i = null
                    makeNotificationSound(context)
                }
            } else {
                try {
                    if (data["chat"] != null) {


                        if (!isAppOnForeground(this)) {
                            if (notificationIntent != null) notificationIntent = null
                            notificationIntent = Intent(context, SplashScreenActivity::class.java)
                            notificationIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            notificationIntent?.action = Intent.ACTION_MAIN
                            notificationIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
                            passingNoificationIntent(notificationIntent, context, message, 1)
                        } else {
                            if (notificationIntent != null) notificationIntent = null
                            notificationIntent = Intent(context, DefaultWindowForChat::class.java)
                            notificationIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            notificationIntent?.action = Intent.ACTION_MAIN
                            notificationIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
                            val intent =
                                PendingIntent.getActivity(context, 0, notificationIntent,  PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                            val soundUri =
                                Uri.parse("android.resource://com.zoom2u_customer/"// +R.raw.chatnotification
                                         )
                           notificationChannelFor8AmdLower(
                                context,
                                intent,
                                "Zoom2u",
                                message,
                                soundUri,
                                1
                            )
                        }
                    } else {
                        if (notificationMessage != "") {
                            callDialogNotification(context, isRunning, message)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    if (notificationMessage != "") {
                        callDialogNotification(context, isRunning, message)
                    }
                }
            }

    }
    private fun makeNotificationSound(context: Context) {
        try {
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notification = Notification.Builder(context).build()
            notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
            notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
            notification.defaults = notification.defaults or Notification.DEFAULT_VIBRATE
            notificationManager.notify(0, notification)
        } catch (e1: java.lang.Exception) {
            e1.printStackTrace()
        }
    }

    private fun appISRunning(): Boolean {
        try {
            val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
            // get the info from the currently running task
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo?.packageName.equals(
                    "com.zoom2u_customer",
                    ignoreCase = true
                )
            ) return true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return false
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun passingNoificationIntent(
        newIntent: Intent?,
        context: Context,
        message: String?,
        isChatNotification: Int
    ) {
        val title = "Zoom2u"

        //************** Notification change for Android O (8.0) target and lower ************
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = PendingIntent.getActivity(context, 0, newIntent,  PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        notificationChannelFor8AmdLower(
            context,
            intent,
            title,
            message,
            soundUri,
            isChatNotification
        )
    }


    private fun isAppOnForeground(context: MyFcmListenerService): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == "com.zoom2u_customer") {
                return true
            }
        }
        return false
    }

    private fun isAppPresentInBG(context: MyFcmListenerService) {
        try {
            val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            // get the info from the currently running task
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.appTasks.size == 0)
                    isAppPresentInBG = false
                    else
                        isAppPresentInBG=true
            } else {
                val taskInfo = am.getRunningTasks(Int.MAX_VALUE)
                for (i in taskInfo.indices) {
                    if (taskInfo[i].baseActivity!!.packageName == "com.zoom2u_customer") {
                        isAppPresentInBG = true
                        break
                    } else isAppPresentInBG = false
                }
            }
        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun callDialogNotification(context: Context, isRunning: Boolean, message: String?) {
        if (!isAppPresentInBG) {
            if (notificationIntent != null) notificationIntent = null
            notificationIntent = Intent(context, SplashScreenActivity::class.java)
            notificationIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            notificationIntent?.action = Intent.ACTION_MAIN
            notificationIntent?.addCategory(Intent.CATEGORY_LAUNCHER)
            passingNoificationIntent(notificationIntent, context, message, 0)
        } else {
            var i: Intent? = Intent("android.intent.action.MAIN")
            i?.setClass(context, DialogActivity1::class.java)
            i?.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(i)
            i = null
            makeNotificationSound(context)

        }
    }

}