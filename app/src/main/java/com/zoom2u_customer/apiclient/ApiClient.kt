package com.zoom2u_customer.apiclient

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ApiClient{

    companion object {


           // val BaseUrl = "https://api-test.zoom2u.com/"
            //val BaseUrl = "https://api-staging.zoom2u.com/"
            val BaseUrl = "https://api.zoom2u.com/"
            private lateinit var retrofit: Retrofit
        var gson = GsonBuilder()
            .setLenient()
            .create()

        private fun getInstance(): Retrofit {

             retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                 .addConverterFactory(ScalarsConverterFactory.create())

                 .client(initOkHttpBuilder().build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BaseUrl)
                .build()

            return retrofit
        }

        private fun initOkHttpBuilder(): OkHttpClient.Builder {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)
            return builder
        }

        fun getServices(): ServiceApi {

            return getInstance().create(ServiceApi::class.java)
        }
    }
}