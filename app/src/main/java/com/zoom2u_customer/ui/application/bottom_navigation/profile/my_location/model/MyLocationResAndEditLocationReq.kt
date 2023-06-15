package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyLocationResAndEditLocationReq(
    var DefaultDropoff: Boolean?=null,
    var DefaultPickup: Boolean?=null,
    val Location: location?=null,
    val PreferredLocationId: Int?=null
) : Parcelable {
    @Parcelize
    data class location(
        val `$id`: String?=null,
        val `$type`: String?=null,
        var Address: String?=null,
        var GPSX: Double?=null,
        var GPSY: Double?=null,
        var ContactName: String?=null,
        var Phone: String?=null,
        var Email: String?=null,
        var Notes: String?=null,
        var Suburb: String?=null,
        val GpsCoordinates: gpsCoordinates?=null,
        var UnitNumber: String?=null,
        var StreetNumber: String?=null,
        var Street: String?=null,
        var State: String?=null,
        var Postcode: String?=null,
        var CompanyName: String?=null
    ): Parcelable  {
        @Parcelize
        data class gpsCoordinates(
            val `$id`: String?=null,
            val `$type`: String?=null,
            val Geography: geography?=null
        ) :Parcelable {
            @Parcelize
            data class geography(
                val `$id`: String?=null,
                val `$type`: String?=null,
                val CoordinateSystemId: Int?=null,
                val WellKnownText: String?=null
            ): Parcelable
        }
    }
}