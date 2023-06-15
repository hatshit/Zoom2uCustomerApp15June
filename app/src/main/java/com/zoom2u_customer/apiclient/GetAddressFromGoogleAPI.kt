package com.zoom2u_customer.apiclient

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GetAddressFromGoogleAPI {


    companion object {

        private val PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/autocomplete/json"
        private val DIRECTION_API_BASE = "https://maps.googleapis.com/maps/api/directions/json?"
        private val GEOCODER_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?"
        private const val BaseUrl ="https://maps.googleapis.com/maps/api/"

        private lateinit var retrofit: Retrofit

        private fun getInstance(): Retrofit {

            retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(initOkHttpBuilder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BaseUrl)
                .build()

            return retrofit
        }

        private fun initOkHttpBuilder(): OkHttpClient.Builder {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(60, TimeUnit.SECONDS)
            builder.readTimeout(60, TimeUnit.SECONDS)
            return builder
        }

        fun getGoogleServices(): GoogleServiceApi {
            return getInstance().create(GoogleServiceApi::class.java)
        }
    }






}