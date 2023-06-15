package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddLocationReq(
    var DefaultDropoff: Boolean? = null,
    var DefaultPickup: Boolean? = null,
    //var location: Location1? = null,
    var Location: Location2? = null
) : Parcelable {

    @Parcelize
    data class Location2(
        var Address: String? = null,
        var CompanyName: String? = null,
        var ContactName: String? = null,
        var Country: String? = null,
        var Email: String? = null,
        var Gpsx: Double? = null,
        var Gpsy: Double? = null,
        var Notes: String? = null,
        var Phone: String? = null,
        var Postcode: String? = null,
        var State: String? = null,
        var Street: String? = null,
        var StreetNumber: String? = null,
        var Suburb: String? = null,
        var UnitNumber: String? = null,
        ) : Parcelable

}


