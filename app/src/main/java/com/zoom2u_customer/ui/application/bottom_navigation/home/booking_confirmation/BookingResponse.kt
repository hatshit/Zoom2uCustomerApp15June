package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookingResponse(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var BookingId: String? = null,
    var BookingRef: String? = null,
    var DeliverySpeed: String? = null,
    var Verified: Boolean? = null

): Parcelable