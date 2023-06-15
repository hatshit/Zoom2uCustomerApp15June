package com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat

import android.view.View
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase


import java.util.*

class ActiveBidLoadChatBookingArray(arrayOfChat :MutableList<ActiveBidChatList>) {

    init {
        arrayOfChatDelivery = arrayOfChat
        if (arrayOfChatDelivery.size > 0) {
            for (model_deliveriesToChat in arrayOfChatDelivery) {
                FirebaseDatabase.getInstance().reference.child("quote-request-comments/" + model_deliveriesToChat.bookingId.toString() + "_" + model_deliveriesToChat.courierId.toString() + "/status/courier/unread")
                    .removeEventListener(model_deliveriesToChat.valueEventListnerOfCourierChat!!)
                FirebaseDatabase.getInstance().reference.child("quote-request-comments/" + model_deliveriesToChat.bookingId.toString() + "_" + model_deliveriesToChat.courierId.toString() + "/status/customer/unread")
                    .removeEventListener(model_deliveriesToChat.valueEventListnerOfCustomerChat!!)
            }
        }
    }

    companion object {
        var arrayOfChatDelivery: MutableList<ActiveBidChatList> = ArrayList()
        var customerID = ""
        var notifyChatIcon: TextView? = null


        /** Show Notify icon if unread chat available ******/
        fun showNotifyIconForUnreadChat(countChatMapView: TextView?) {
            setCourierToOnlineForChat()
            showIconForUnreadChat(countChatMapView)
        }


        /** Set courier status to online for chat **/
        fun setCourierToOnlineForChat() {
            if (customerID != "")
                FirebaseDatabase.getInstance().reference.child("/customers/$customerID/status/online")
                    .setValue(1)
        }

        /*  Set courier status to offline for chat */
        fun setCourierToOfflineFromChat() {
            if (customerID != "")
                FirebaseDatabase.getInstance().reference.child(
                    "/customers/$customerID/status/online"
                ).onDisconnect().setValue(0)
        }


        /** Show exclamation icon to unread chat for courier
         * Called in OnResume or Booking view selection   */
        private fun showIconForUnreadChat(exclamationTxt: TextView?) {
            var totalUnreadCount = 0
            if (exclamationTxt != null) {
                notifyChatIcon = exclamationTxt
                for (modelDeliveryChat in arrayOfChatDelivery) {
                    totalUnreadCount += modelDeliveryChat.unreadMsgCountOfCourier
                }
                if (totalUnreadCount > 0) {
                    exclamationTxt.visibility = View.VISIBLE
                    exclamationTxt.text = totalUnreadCount.toString()
                }
                else {
                    exclamationTxt.visibility = View.GONE
                }


            }
        }

    }
}