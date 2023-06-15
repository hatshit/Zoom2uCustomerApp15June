package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class InterStateRes(
    var `$id`: String? = null,
    var `$type`: String? = null,
    var Vehicle: String? = null,
    var Distance: String? = null,
    var DistanceInMeters: Int? = null,
    var QuoteOptions: List<QuoteOptionClass>? = null,
    var IsCustomerRegistered: Boolean? = null,
    var PricingScheme: String? = null,
    var PricingChangeHistoryId: Int? = null,
    var PickupLatitude: String? = null,
    var PickupLongitude: String? = null,
    var DropLatitude: String? = null,
    var DropLongitude: String? = null

) : Parcelable