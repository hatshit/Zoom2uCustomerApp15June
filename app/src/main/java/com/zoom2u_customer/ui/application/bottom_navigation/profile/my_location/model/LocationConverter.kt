package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model


import androidx.room.TypeConverter
import com.google.gson.Gson



class LocationConverter {
    private val gson: Gson = Gson()

    @TypeConverter
    fun stringToList(data: String?): MyLocationResAndEditLocationReq.location {
        val location:MyLocationResAndEditLocationReq.location= gson.fromJson(data, MyLocationResAndEditLocationReq.location::class.java)
        return location
    }

    @TypeConverter
    fun listToString(location: MyLocationResAndEditLocationReq.location): String? {
        return gson.toJson(location)
    }
}
