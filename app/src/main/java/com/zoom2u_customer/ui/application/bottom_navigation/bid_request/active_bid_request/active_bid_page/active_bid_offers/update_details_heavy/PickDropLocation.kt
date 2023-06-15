package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickDropLocation(
    var Address: String? = null,
    var CompanyName: String? = null,
    var ContactName: String? = null,
    var Email: String? = null,
    var GPSX: String? = null,
    var GPSY: String? = null,
    var GpsCoordinates: String? = null,
    var IsFlexible: Boolean? = null,
    var Notes: String? = null,
    var Phone: String? = null,
    var Postcode: String? = null,
    var State: String? = null,
    var Street: String? = null,
    var StreetNumber: String? = null,
    var Suburb: String? = null,
    var UnitNumber: String? = null,

) : Parcelable