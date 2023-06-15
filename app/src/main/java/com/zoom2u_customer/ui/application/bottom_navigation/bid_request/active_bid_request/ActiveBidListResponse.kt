package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request

import android.os.Parcelable
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.ShipmentsClass
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActiveBidListResponse(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var Id: Int? = null,
    var QuoteRef: String? = null,
    var CreatedDateTime: String? = null,
    var ItemCategory: String? = null,
    var Shipments: MutableList<ShipmentsClass>? = null,
    var CustomerName: String? = null,
    var CustomerCompany: String? = null,
    var Status: String? = null,
    var PickupSuburb: String? = null,
    var PickupAddress: String? = null,
    var DropAddress: String? = null,
    var PickupState: String? = null,
    var PickupDateTime: String? = null,
    var DropDateTime: String? = null,
    var DropSuburb: String? = null,
    var DropState: String? = null,
    var Title: String? = null,
    var Notes: String? = null,
    var ItemType: String? = null,
    var Offers: Int? = null,
    var IsInvalidAddress: Boolean? = null,
    var Source: String? = null
) : Parcelable


