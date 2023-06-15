package com.zoom2u_customer.ui.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RateMeResponse(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var BookingId: String? = null,
    var CourierName: String? = null,
    var CourierImage: String? = null,
    var CourierMobile: String? = null,
    var PickupSuburb: String? = null,
    var DropSuburb: String? = null,
    var PickupAddress: String? = null,
    var DropAddress: String? = null,
    var SecondaryRating: String? = null,
    var BookingRef: String? = null,
    var Status: String? = null,
  )  : Parcelable

