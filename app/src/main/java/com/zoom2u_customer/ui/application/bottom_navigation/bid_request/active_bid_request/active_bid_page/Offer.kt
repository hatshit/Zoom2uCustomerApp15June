package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Offer(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var OfferId: Int? = null,
    var Price: Double? = null,
    var Courier: String? = null,
    var CourierImage: String? = null,
    var CustomerImage: String? = null,
    var QuoteDateTime: String? = null,
    var PickupETA: String? = null,
    var TotalBookings: Int? = null,
    var TotalBookingsPercentage: Float? = null,
    var TotalThumbsUps: Int? = null,
    var TotalThumbsDown: Int? = null,
    var DropETA: String? = null,
    var CourierId: String? = null,
    var Notes: String? = null,
    var BidActivePeriod: String? = null,
    var IsCancel: Boolean? = null,
    var RegisteredWithZoom2uOnDateTime: String? = null,
    var LastCompletedDeliveryDateTime: String? = null,
    var CarrierPrice: Int? = null
) : Parcelable
{


}