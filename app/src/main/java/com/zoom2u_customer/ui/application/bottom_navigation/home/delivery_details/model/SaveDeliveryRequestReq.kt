package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class SaveDeliveryRequestReq(
    var _authorityToLeaveForm: AuthorityToLeaveForm? = null,
    var _deliveryRequestModel: DeliveryRequestModel? = null,
    var _interstateModel: InterstateModel? = null,
    var _shipmentModel: List<ShipmentsClass>? = null,
) : Parcelable {
    @Parcelize
    data class AuthorityToLeaveForm(
        var LeaveAt: String? = null,
        var Instructions: String? = null,
        var ReceiverName: String? = null,
        var DoorCode: String? = null,
        var NoContact: Boolean? = null
    ) : Parcelable


    @Parcelize
    data class DeliveryRequestModel(
        var Items: List<Item>? = null,
        var FreightTitle: String? = null,
        var TrailerType: String? = null,
        var TrailerTypeNote: String? = null,
        var LoadType: String? = null,
        var FreightValue: String? = null,
        var SecurityIdCardNumber: String? = null,
        var PickupLocationPremisesType: String? = null,
        var DropLocationPremisesType: String? = null,
        var PickupLocationTailLiftType: String? = null,
        var DropLocationTailLiftType: String? = null,
        var PickupLocationTailLiftNotes: String? = null,
        var DropLocationTailLiftNotes: String? = null,
        var Status: String? = null,
        var Source: String? = null,
        var FreightCategory: Int? = null,
        var OtherDetails: String? = null,
        var PickupLocation: PickupLocationClass? = null,
        var DropLocation: DropLocationClass? = null,
        var Vehicle: String? = null,
        var PickupDateTime: String? = null,
        var DropDateTime: String? = null,
        var RequestedPickupDateTimeWindowStart: String? = null,
        var RequestedPickupDateTimeWindowEnd: String? = null,
        var RequestedDropDateTimeWindowStart: String? = null,
        var RequestedDropDateTimeWindowEnd: String? = null,
        var DeliverySpeed: String? = null,
        var Price: Int? = null,
        var Notes: String? = null,
        var Distance: String? = null,
        var IsInterstate: Boolean? = null,
        var BookingFee: Int? = null,
        var IsPayByInvoiceMarked: Boolean? = null,
        var IsRentContainer: Boolean? = null,
        var SendSmsToPickupPerson: Boolean? = null,
        var DropIdentityType: String? = null,
        var DropIdentityNumber: String? = null,
        var IsDropIdCheckRequired: Boolean? = null,
        var AuthorityToLeave: Boolean? = null,
        var LeaveAt: String? = null,
        var Instructions: String? = null,
        var IsCreatedFromQuotes: Boolean? = null,
        var OrderNumber: String? = null,
        var PackagingNotes: String? = null,
        var Weight: String? = null,
        var IsNoContactPickup: Boolean? = null,
        var IsNoContactDrop: Boolean? = null,
        var IsPickupAddressManuallyEntered: Boolean? = null,
        var IsDropAddressManuallyEntered: Boolean? = null,
        var IsDrivable: Boolean? = null,
        var IsVehicleWithBelongings: Boolean? = null,
        var IsEmptyVehicle: Boolean? = null,
        var VehicleBrand: String? = null,
        var VehicleModel: String? = null,
        var IsLaptopOrMobile: String? = null,
        var IsSuggestedPrice: Boolean? = null,
        var DeclarationSignature: String? = null,
        var PricingScheme: String? = null,
        var PricingPlanChangeHistoryId: Int? = null,
        var PaymentNonce: String? = null,
    ) : Parcelable {
        @Parcelize
        data class Item(
            var ContainerSize: String? = null,
            var Packaging: String? = null,
            var Quantity: String? = null,
            var LengthCm: String? = null,
            var WidthCm: String? = null,
            var HeightCm: String? = null,
            var ItemWeightKg: String? = null,
            var TotalWeightKg: String? = null
        ) : Parcelable

        @Parcelize
        data class PickupLocationClass(
            var ContactName: String? = null,
            var Phone: String? = null,
            var Email: String? = null,
            var Address: String? = null,
            var Notes: String? = null,
            var Gpsx: String? = null,
            var Gpsy: String? = null,
            var UnitNumber: String? = null,
            var StreetNumber: String? = null,
            var Street: String? = null,
            var Suburb: String? = null,
            var State: String? = null,
            var Postcode: String? = null,
            var PremisesType: String? = null,
            var IsFlexible: Boolean? = null,
            var CompanyName: String? = null,
            var Country: String? = null
        ) : Parcelable

        @Parcelize
        data class DropLocationClass(
            var ContactName: String? = null,
            var Phone: String? = null,
            var Email: String? = null,
            var Address: String? = null,
            var Notes: String? = null,
            var Gpsx: String? = null,
            var Gpsy: String? = null,
            var UnitNumber: String? = null,
            var StreetNumber: String? = null,
            var Street: String? = null,
            var Suburb: String? = null,
            var State: String? = null,
            var Postcode: String? = null,
            var PremisesType: String? = null,
            var IsFlexible: Boolean? = null,
            var CompanyName: String? = null,
            var Country: String? = null
        ) : Parcelable
    }

    @Parcelize
    data class ShipmentModel(
        var Category: String? = null,
        var Quantity: Int? = null,
        var Value: Int? = null,
        var LengthCm: Int? = null,
        var HeightCm: Int? = null,
        var WidthCm: Int? = null,
        var ItemWeightKg: Float? = null,
        var TotalWeightKg: String? = null
    ) : Parcelable

    @Parcelize
    data class InterstateModel(
        var RoutePickupPrice: Int? = null,
        var RouteAirPrice: Int? = null,
        var RouteDropPrice: Int? = null,
        var PickupDistance: String? = null,
        var DropDistance: String? = null,
    ) : Parcelable

}