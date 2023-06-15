package com.zoom2u_customer.apiclient

import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GoogleServiceApi {

    @GET
    fun getAddressFromGeocoder(@Url url:String): Single<Response<JsonObject>>



}