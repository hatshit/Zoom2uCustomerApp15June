package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickUpDetails(

    var pickUnit: String? = null,
    var pickStreet: String? = null,
    var pickName: String? = null,
    var pickPhone: String? = null,
    var pickEmail: String? = null,
    var pickAddress: String? = null,
    var pickInstruction: String? = null,
    var pickGpx: String? = null,
    var pickGpy: String? = null,
    var pickStreetNumber: String? = null,
    var pickSuburb: String? = null,
    var pickState: String? = null,
    var pickPostCode: String? = null,
    var pickPremisesType: String? = null,
    var pickCompany: String? = null,
    var pickCountry: String? = null,
    var sendSmsToPickupPerson: Boolean? = null,
    var isNoContactPickup: Boolean? = null,
    var PickupLocationTailLiftNotes:String?=null,
    var PickupLocationTailLiftType:String?=null,

): Parcelable