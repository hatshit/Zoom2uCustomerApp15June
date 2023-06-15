package com.zoom2u_customer.ui.application.bottom_navigation.history.history_details

import android.os.Parcelable
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.ShipmentsClass
import kotlinx.parcelize.Parcelize


@Parcelize
data class HistoryDetailsResponse(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var BookingId: Int? = null,
    var BookingRef: String? = null,
    var CustomerFirstName: String? = null,
    var CustomerLastName: String? = null,
    var CustomerMobile: String? = null,
    var CustomerEmail: String? = null,
    var CustomerCompany: String? = null,
    var CourierId: String? = null,
    var CourierFirstName: String? = null,
    var CourierLastName: String? = null,
    var CourierMobile: String? = null,
    var CourierEmail: String? = null,
    var CourierCompany: String? = null,
    var CourierLastUpdated: String? = null,
    var CourierVehicle: String? = null,
    var CourierCurrentLocation: CourierCurrentLocationClass? = null,
    var CourierCurrentLocationGeogaphy: CourierCurrentLocationGeographyClass? = null,
    var PickupSuburb: String? = null,
    var PickupAddress: String? = null,
    var PickupEmail: String? = null,
    var PickupNotes: String? = null,
    var PickupPhone: String? = null,
    var PickupSignature: String? = null,
    var PickupContactName: String? = null,
    var DeliveryPickupContactName: String? = null,
    var PickupDateTime: String? = null,
    var PickupLocation: String? = null,
    var PickupLocationCompanyName: String? = null,
    var DropSuburb: String? = null,
    var DropAddress: String? = null,
    var DropEmail: String? = null,
    var DropNotes: String? = null,
    var DropPhone: String? = null,
    var DropContactName: String? = null,
    var DropPhoto: String? = null,
    var DropSignature: String? = null,
    var DropDateTime: String? = null,
    var DropLocation: String? = null,
    var DeliveryDropContactName: String? = null,
    var DropLocationCompanyName: String? = null,
    var Price: Double? = null,
    var CourierPrice: Int? = null,
    var Status: String? = null,
    var CarrierId: Int? = null,
    var PackageImage: String? = null,
    var PackageNotes: String? = null,
    var Package: String? = null,
    var DeliverySpeed: String? = null,
    var Vehicle: String? = null,
    var Invoice: String? = null,
    var InvoiceId: Int? = null,
    var InvoiceNumber: String? = null,
    var Rating: Int? = null,
    var PickupETA: String? = null,
    var DropETA: String? = null,
    var PickupActual: String? = null,
    var DropActual: String? = null,
    var Latitude: String? = null,
    var Longitude: String? = null,
    var IsOnHold: Boolean? = null,
    var IsInterstate: Boolean? = null,
    var BookingCallHistory: List<BookingCallHistoryClass>? = null,
    var DeliveryShipments: MutableList<ShipmentsClass>? = null,
    var OrderNumber: String? = null,
    var IsCancel: Boolean? = null,
    var PickupSigneePosition: Int? = null,
    var DropSigneePosition: String? = null,
    var DropIdentityType: String? = null,
    var DropIdentityNumber: String? = null,
    var CarrierType: String? = null,
    var RoutePolyline: String? = null,
    var RequestedDropDateTimeWindowStart: String? = null,
    var RequestedDropDateTimeWindowEnd: String? = null,
    var RequestedPickupDateTimeWindowStart: String? = null,
    var RequestedPickupDateTimeWindowEnd: String? = null,
    var DeclarationSignature: String? = null,
    var Source: String? = null,
    var TrackingLink: String? =null,
    var ThirdPartyCarrierBookingReference: String? = null,
    var ThirdPartyCarrierLabelUrl: String? = null,
    var ThirdPartyCarrierConsignmentNumber: String? = null,
    var ThirdPartyCarrierTrackingLink: String? = null
) : Parcelable {

    @Parcelize
   data class BookingCallHistoryClass(
       var `$id`: String? = null,
       var `$type`: String? = null,
       var BookingCallHistoryId: Int? = null,
       var CreatedDateTime: String? = null,
       var Notes: String? = null,
       var BookingId: Int? = null,
       var UserId: String? = null,
       var DeliveryRequest: String? = null
   ): Parcelable








    @Parcelize
    data class CourierCurrentLocationClass(
        var `$id`: String? = null,
        var `$type`: String? = null,
        var lat: Double? = null,
        var lng: Double? = null
    ) : Parcelable


    @Parcelize
    data class CourierCurrentLocationGeographyClass(
        var `$id`: String? = null,
        var `$type`: String? = null,
        var Geography: GeographyClass? = null,
    ) : Parcelable {
        @Parcelize
        data class GeographyClass(
            var `$id`: String? = null,
            var `$type`: String? = null,
            var coordinateSystemId: Int? = null,
            var wellKnownText: String? = null
        ) : Parcelable
    }

}