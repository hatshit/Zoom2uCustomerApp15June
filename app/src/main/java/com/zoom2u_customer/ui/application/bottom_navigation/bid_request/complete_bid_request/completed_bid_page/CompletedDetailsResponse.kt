package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page

import android.os.Parcelable
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.ShipmentsClass
import kotlinx.parcelize.Parcelize


@Parcelize
data class CompletedDetailsResponse(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var Id: Int? = null,
    var QuoteRef: String? = null,
    var CustomerName: String? = null,
    var CustomerCompany: String? = null,
    var CustomerMobile: String? = null,
    var CustomerEmail: String? = null,
    var Notes: String? = null,
    var CreatedDateTime: String? = null,
    var PickupDateTime: String? = null,
    var DropDateTime: String? = null,
    var Distance: String? = null,
    var PickupContact: String? = null,
    var DropContact: String? = null,
    var PickupPhone: String? = null,
    var DropPhone: String? = null,
    var PickupEmail: String? = null,
    var DropEmail: String? = null,
    var PickupLatitude: String? = null,
    var PickupLongitude: String? = null,
    var DropLatitude: String? = null,
    var DropLongitude: String? = null,
    var PickupSuburb: String? = null,
    var DropSuburb: String? = null,
    var PickupState: String? = null,
    var DropState: String? = null,
    var PickupAddress: String? = null,
    var DropAddress: String? = null,
    var PackagingNotes: String? = null,
    var PackageImages: List<String>? = null,
    var Offers: List<Offer>? = null,
    var CallNotes: List<String>? = null,
    var Status: String? = null,
    var IsSuggestedPrice: Boolean? = null,
    var Price: Int? = null,
    var MinPrice: Int? = null,
    var MaxPrice: Int? = null,
    var AverageBid: Int? = null,
    var AcceptedOfferId: String? = null,
    var Shipments: MutableList<ShipmentsClass>? = null,
    var TotalBids: Int? = null,
    var CustomerPaymentMethod: String? = null,
    var BookingId: Int? = null,
    var Source: String? = null,
    var PickupLocation: PickupLocationClass? = null,
    var DropLocation: DropLocationClass? = null,
    var PurchaseOrderNumber: String? = null,
    var IsCreatedFromQuotes: Boolean? = null,
    var Origin: String? = null,
    var RoutePolyline: String? = null,
    var PickupCompanyName: String? = null,
    var DropCompanyName: String? = null,
    var RerequestRef: String? = null
) : Parcelable{

    @Parcelize
    data class PickupLocationClass(
        var `$id`: String? = null,
        var `$type`: String? = null,
        var Address: String? = null,
        var CompanyName: String? = null,
        var ContactName: String? = null,
        var Email: String? = null,
        var GPSX: String? = null,
        var GPSY: String? = null,
        var Notes: String? = null,
        var Phone: String? = null,
        var Postcode: String? = null,
        var State: String? = null,
        var StreetNumber: String? = null,
        var Suburb: String? = null,
        var UnitNumber: String? = null,
        var Street: String? = null
    ): Parcelable


    @Parcelize
    data class DropLocationClass(
        var `$id`: String? = null,
        var `$type`: String? = null,
        var Address: String? = null,
        var CompanyName: String? = null,
        var ContactName: String? = null,
        var Email: String? = null,
        var GPSX: String? = null,
        var GPSY: String? = null,
        var Notes: String? = null,
        var Phone: String? = null,
        var Postcode: String? = null,
        var State: String? = null,
        var StreetNumber: String? = null,
        var Suburb: String? = null,
        var UnitNumber: String? = null,
        var Street: String? = null
    ): Parcelable



}



