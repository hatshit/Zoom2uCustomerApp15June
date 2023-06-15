package com.zoom2u_customer.ui.application.chat.message

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Chat(
    var message: String? = null,
    var sender: String? = null,
    var receipt: Int? = null,
    var timestamp: String? = null,
  ): Parcelable
