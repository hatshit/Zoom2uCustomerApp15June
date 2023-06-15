package com.zoom2u_customer.ui.application.chat.bid_chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class BidChat(
    var message: String? = null,
    var sender: String? = null,
    var receipt: Int? = null,
    var timestamp: String? = null,
    var user:String?=null
): Parcelable
