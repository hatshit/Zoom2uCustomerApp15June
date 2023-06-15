package com.zoom2u_customer.apiclient


import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*


interface ServiceApi {


    @GET
    fun brainTreeApiCall(@Url url:String, @HeaderMap  map:Map<String, String>):Single<Response<ResponseBody>>


    @FormUrlEncoded
    @POST("token")
    fun getLoginData(@FieldMap stringMap: Map<String, String> ):Single<Response<JsonObject>>


    @POST("api/account/registerCustomer")
    fun getSignUpData(@Body stringMap: Map<String, String>  ):Single<Response<JsonObject>>

    @POST
    fun reSetPassword(@Url url:String):Single<Response<JsonObject>>

    @POST
    fun postWithJsonObject(@Url url:String, @HeaderMap  map:Map<String, String>):Single<Response<JsonObject>>

    @GET
    fun getWithJsonObject(@Url url:String, @HeaderMap  map:Map<String, String>):Single<Response<JsonObject>>

    @GET
    fun getWithJsonArray(@Url url:String, @HeaderMap  map:Map<String, String>):Single<Response<JsonArray>>

    @POST
    fun postBodyJsonObject(@Url url:String, @HeaderMap  map:Map<String, String>, @Body request:JsonObject?):Single<Response<JsonObject>>

    @POST
    fun postWithJsonBody(@Url url:String ,@HeaderMap  map:Map<String, String>,@Body request:JsonObject?):Single<Response<JsonArray>>

    @POST
    fun bookingApiCall(@Url url:String, @HeaderMap  map:Map<String, String>, @Body request: JSONObject?):Single<Response<JsonObject>>


    @Multipart
    @POST
    fun changeDp(@Url url:String, @HeaderMap  map:Map<String, String>, @Part image: MultipartBody.Part,@Part("Photo") photo: RequestBody):Single<Response<JsonObject>>

    @POST
    fun cancelBooking(@Url url:String, @HeaderMap  map:Map<String, String>):Single<Response<Void>>

    @Multipart
    @POST
    fun quoteImageUpload(@Url url:String, @HeaderMap  map:Map<String, String>, @Part image: MutableList<MultipartBody.Part>):Single<Response<JsonObject>>



    @GET
    fun getWithJsonArrayNoHeader(@Url url:String):Single<Response<JsonArray>>

}
