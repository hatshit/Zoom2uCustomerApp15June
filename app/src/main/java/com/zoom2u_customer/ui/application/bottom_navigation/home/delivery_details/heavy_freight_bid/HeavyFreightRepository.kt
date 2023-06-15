package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

class HeavyFreightRepository (private var serviceApi: ServiceApi, var context: Context?) {

    fun getQuoteRequest(
        jObjForPlaceBooking: JSONObject?,
        arrayImage:MutableList<String>?,
        itemCategory: String?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (imagePath:MutableList<String>?) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject(
                    "breeze/HeavyFreight/SaveHeavyFreightQuote",
                    AppUtility.getApiHeaders(),
                    JsonParser.parseString(jObjForPlaceBooking.toString()) as JsonObject
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                arrayImage?.add(responce.body()?.get("QuoteRequestId").toString())
                                arrayImage?.add(responce.body()?.get("HeavyFreightQuoteRef").toString())
                                arrayImage?.add(itemCategory.toString())
                                onSuccess(arrayImage)
                            }else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show()
                            }

                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                "Something went wrong please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            )
        } else {
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }


    fun uploadQuoteImages(
        requestId:Int?,
        heavyFreightQuoteRef:String?,
        arrayImage:MutableList<String>?,
        itemCategory: String?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            val multipartBodyList:MutableList<MultipartBody.Part> = ArrayList()
            for (imagePath in arrayImage!!) {
                val file: File = File(imagePath)
                val requestFile: RequestBody =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val body: MultipartBody.Part=
                    MultipartBody.Part.createFormData("image", file.name, requestFile)
                multipartBodyList.add(body)
            }


            disposable.add(
                serviceApi.quoteImageUpload(
                    "/api/upload/UploadDocForHeavyFreightQuote?QuoteRequestId=$requestId",
                    AppUtility.getApiHeaders(),
                    multipartBodyList
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                var response:String = heavyFreightQuoteRef.toString()
                                response=response.plus("?").plus(itemCategory)
                                onSuccess(response)
                            } else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Log.d("", "")
                            Toast.makeText(
                                context,
                                "Something went wrong please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            )
        } else {
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }

}