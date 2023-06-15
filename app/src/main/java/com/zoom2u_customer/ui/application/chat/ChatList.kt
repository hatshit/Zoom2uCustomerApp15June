package com.zoom2u_customer.ui.application.chat

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zoom2u_customer.R
import com.zoom2u_customer.services.DefaultWindowForChat
import com.zoom2u_customer.services.MyFcmListenerService
import com.zoom2u_customer.ui.application.chat.message.Chat
import com.zoom2u_customer.utility.AppUtility
import org.json.JSONObject
import java.util.*

class ChatList : Parcelable {
    var bookingId = 0
    var bookingRef: String? = null
    var customerId: String? = null
    var courierId: String? = null
    var courier: String? = null
    var courierImage: String? = null
    var customerImage: String? = null
    var pickupSuburb: String? = null
    var dropSuburb: String? = null
    var dropDateTime: String? = null
    var lastUpdatedTime: String? = null
    var unreadMsgCountOfCourier = 0
    var unreadMsgCountOfCustomer = 0
    var courierMobile: String? = null
    var dropETA: String? = null
    var valueEventListnerOfCourierChat: ValueEventListener? = null
    var valueEventListnerOfCustomerChat: ValueEventListener? = null
    var valueEventListnerForCourierOnline: ValueEventListener? = null
    var context: Context? = null
    var isNotificationSoundPlay = false
    var isCourierOnline: Long = 0
    constructor(context: Context?, jObjOfChatDelivery: JSONObject) : super() {
        try {
            this.context = context
            bookingId = jObjOfChatDelivery.getInt("BookingId")
            bookingRef = jObjOfChatDelivery.getString("BookingRef")
            customerId = jObjOfChatDelivery.getString("CustomerId")
            courierId = jObjOfChatDelivery.getString("CourierId")
            courier = jObjOfChatDelivery.getString("Courier")
            pickupSuburb = jObjOfChatDelivery.getString("PickupSuburb")
            dropSuburb = jObjOfChatDelivery.getString("DropSuburb")
            courierImage = jObjOfChatDelivery.getString("CourierImage")
            customerImage = jObjOfChatDelivery.getString("CustomerImage")
            dropDateTime = jObjOfChatDelivery.getString("DropDateTime")
            lastUpdatedTime = ""
            courierMobile = jObjOfChatDelivery.getString("CourierMobile")
            dropETA = jObjOfChatDelivery.getString("DropETA")
            getUnreadMsgCountOfBookingId(
                "customer-courier-booking-chat/" + jObjOfChatDelivery.getInt("BookingId") + "_"
                        + jObjOfChatDelivery.getString("CourierId") + "/status", "/courier"
            )
            getCourierOnline("/couriers/" + jObjOfChatDelivery.getString("CourierId") + "/status/online")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCourierOnline(childUrlForStatusUpdate: String) {
        addMessageEventListnerForCourierOnline(childUrlForStatusUpdate)
    }

    private fun addMessageEventListnerForCourierOnline(childUrlForStatusUpdate: String) {
        valueEventListnerForCourierOnline = object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                try {
                    var unreadMsgAdmin: Long = 0
                    if (arg0.value != null) {
                        unreadMsgAdmin = arg0.value as Long
                        isCourierOnline = unreadMsgAdmin
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        }
        FirebaseDatabase.getInstance().reference.child(childUrlForStatusUpdate)
            .addValueEventListener(valueEventListnerForCourierOnline as ValueEventListener)
    }

    private fun addMessageEventListnerOfCustomerChat(childUrlForStatusUpdate: String) {
        valueEventListnerOfCourierChat = object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                try {
                    var unreadMsgAdmin: Long = 0
                    if (arg0.value != null) {
                        unreadMsgAdmin = arg0.value as Long
                        /* Get unread message count of courier message to show unread count in list  */
                        unreadMsgCountOfCustomer = unreadMsgAdmin.toInt()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        }
        FirebaseDatabase.getInstance().reference.child("$childUrlForStatusUpdate/customer/unread")
            .addValueEventListener(valueEventListnerOfCourierChat as ValueEventListener)
    }


    private fun addMessageEventListnerOfCourierChat(
        childUrlForStatusUpdate: String,
        isCustomerOrAdminTxt: String
    ) {
        valueEventListnerOfCustomerChat = object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                if (arg0.value != null) {
                    var unreadCount: Long = 0
                    unreadCount = arg0.value as Long
                    /* Get unread message count of customer or admin and update it to server if courier sent message to this perticular customer  */
                    unreadMsgCountOfCourier = unreadCount.toInt()
                    if (unreadMsgCountOfCourier > 0) {
                        if (context != null) {
                            if (!AppUtility.isAppOnForeground(context)) {
                                val newUrl =
                                    childUrlForStatusUpdate.replace("/status", "")
                                        .trim { it <= ' ' }
                                val valueEventListenerOfNotificcation: ValueEventListener =
                                    object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (postSnapShot in dataSnapshot.children) {
                                                if (!isNotificationSoundPlay) {
                                                    isNotificationSoundPlay = true
                                                    val post =
                                                        postSnapShot.getValue(Chat::class.java)
                                                    val notificationIntent = Intent(
                                                        context,
                                                        DefaultWindowForChat::class.java
                                                    )
                                                    notificationIntent.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                    notificationIntent.action = Intent.ACTION_MAIN
                                                    notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                                                    val pendingIntent = PendingIntent.getActivity(
                                                        context,
                                                        0,
                                                        notificationIntent,
                                                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                                                    )
                                                    val soundUri =
                                                        Uri.parse("android.resource://com.zoom2u_customer/" + R.raw.chatnotification)
                                                    MyFcmListenerService.notificationChannelFor8AmdLower(
                                                        context,
                                                        pendingIntent,
                                                        "Zoom2u",
                                                        post?.message,
                                                        soundUri,
                                                        1
                                                    )
                                                    isNotificationSoundPlay = false
                                                }
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {}
                                    }
                                FirebaseDatabase.getInstance().reference.child("$newUrl/message")
                                    .limitToLast(1)
                                    .addValueEventListener(valueEventListenerOfNotificcation)
                                val removeTaskTimer = Timer()
                               /* removeTaskTimer.schedule(object : TimerTask() {
                                    override fun run() {
                                        FirebaseDatabase.getInstance().reference.child("$newUrl/message")
                                            .limitToLast(1)
                                            .removeEventListener(valueEventListenerOfNotificcation)
                                    }
                                }, 200)*/
                            } else {
                                if (unreadMsgCountOfCourier > 0 && !isNotificationSoundPlay) {
                                    isNotificationSoundPlay = true
                                    if (LoadChatBookingArray.notifyChatIcon != null) {

                                        //send home page
                                        val intent = Intent("home_page")
                                        intent.putExtra("message", "from_active_bid")
                                        LocalBroadcastManager.getInstance(context!!)
                                            .sendBroadcast(intent)

                                        //send bid page
                                        val intent1 = Intent("bid_refresh")
                                        intent1.putExtra("message", "from_base_to_bid")
                                        LocalBroadcastManager.getInstance(context!!)
                                            .sendBroadcast(intent1)

                                        //send history page
                                        val intent2 = Intent("history")
                                        intent2.putExtra("message", "from_base_to_history")
                                        LocalBroadcastManager.getInstance(context!!)
                                            .sendBroadcast(intent2)

                                        //send profile page
                                        val intent3 = Intent("profile")
                                        intent3.putExtra("message", "from_base_to_profile")
                                        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent3)

                                        LoadChatBookingArray.notifyChatIcon?.visibility = View.VISIBLE
                                        LoadChatBookingArray.notifyChatIcon?.text = unreadMsgCountOfCourier.toString()

                                    }
                                    notificationSoundForChat(context)
                                }
                                if (ChatActivity.adapter != null)
                                    ChatActivity.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        }
        FirebaseDatabase.getInstance().reference.child("$childUrlForStatusUpdate$isCustomerOrAdminTxt/unread")
            .addValueEventListener(valueEventListnerOfCustomerChat as ValueEventListener)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(bookingId)
        parcel.writeString(bookingRef)
        parcel.writeString(customerId)
        parcel.writeString(courierId)
        parcel.writeString(courier)
        parcel.writeString(pickupSuburb)
        parcel.writeString(dropSuburb)
        parcel.writeString(dropDateTime)
        parcel.writeString(lastUpdatedTime)
        parcel.writeInt(unreadMsgCountOfCourier)
        parcel.writeInt(unreadMsgCountOfCustomer)
        parcel.writeLong(isCourierOnline)
        parcel.writeString(courierMobile)
        parcel.writeString(dropETA)
        parcel.writeString(courierImage)
        parcel.writeString(customerImage)
    }

    private constructor(`in`: Parcel) {
        bookingId = `in`.readInt()
        bookingRef = `in`.readString()
        customerId = `in`.readString()
        courierId = `in`.readString()
        courier = `in`.readString()
        pickupSuburb = `in`.readString()
        dropSuburb = `in`.readString()
        dropDateTime = `in`.readString()
        lastUpdatedTime = `in`.readString()
        unreadMsgCountOfCourier = `in`.readInt()
        unreadMsgCountOfCustomer = `in`.readInt()
        isCourierOnline = `in`.readLong()
        courierMobile = `in`.readString()
        dropETA = `in`.readString()
        courierImage = `in`.readString()
        customerImage = `in`.readString()
    }

    private fun getUnreadMsgCountOfBookingId(
        childUrlForStatusUpdate: String,
        isCustomerOrAdminTxt: String
    ) {
        addMessageEventListnerOfCourierChat(childUrlForStatusUpdate, isCustomerOrAdminTxt)
        addMessageEventListnerOfCustomerChat(childUrlForStatusUpdate)
    }

    private fun notificationSoundForChat(activitycontext: Context?) {
        try {
            val notification =
                Uri.parse("android.resource://com.zoom2u_customer/" + R.raw.chatnotification)
            val r = RingtoneManager.getRingtone(activitycontext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isNotificationSoundPlay = false
    }

    fun getNotificationIcon(): Int {
        val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.notification_icon else R.drawable.appicon
    }

    companion object CREATOR : Parcelable.Creator<ChatList> {
        override fun createFromParcel(parcel: Parcel): ChatList {
            return ChatList(parcel)
        }

        override fun newArray(size: Int): Array<ChatList?> {
            return arrayOfNulls(size)
        }
    }
}