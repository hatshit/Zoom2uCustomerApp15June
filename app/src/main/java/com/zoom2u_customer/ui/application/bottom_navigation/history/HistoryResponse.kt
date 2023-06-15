package com.zoom2u_customer.ui.application.bottom_navigation.history

import android.os.Parcelable
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.ShipmentsClass
import kotlinx.parcelize.Parcelize

@Parcelize
class HistoryResponse(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var BookingId: Int? = null,
    var BookingRef: String? = null,
    var CustomerEmail: String? = null,
    var CustomerFirstName: String? = null,
    var CustomerLastName: String? = null,
    var CustomerCompany: String? = null,
    var CourierFirstName:String? = null,
    var CourierLastName: String? = null,
    var CourierMobile: String? = null,
    var CourierEmail: String? = null,
    var PickupSuburb: String? = null,
    var PickupDateTime: String? = null,
    var DropSuburb: String? = null,
    var DropAddress: String? = null,
    var PickupAddress :String?=null,
    var DropContactName: String? = null,
    var DropDateTime: String? = null,
    var CreatedDateTime: String? = null,
    var DeliverySpeed: String? = null,
    var Vehicle: String? = null,
    var Price: Float? = null,
    var Status: String? = null,
   // var Rating: Int? = null,
  //  var SecondaryRating: Int? = null,
    var PickupETA: String? = null,
   // var DropETA: String? = null,
    var PickupActual: String? = null,
    var DropActual: String? = null,
    var IsOnHold: Boolean? = null,
    var IsInterstate: Boolean? = null,
   // var DeliveryAttempts: Int? = null,
    var OrderNumber: String? = null,
    var IsCancel: Boolean? = null,
    var Source: String? = null,
    //var CarrierId: Int? = null,
    var CarrierType: String? = null,
    var Shipments: MutableList<ShipmentsClass>? = null,
    var RequestedDropDateTimeWindowStart: String? = null,
    var RequestedDropDateTimeWindowEnd: String? = null,
    var type:Int=0,
    var count:Int=0): Parcelable

