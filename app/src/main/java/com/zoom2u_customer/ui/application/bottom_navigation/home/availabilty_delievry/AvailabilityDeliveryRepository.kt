package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.InterStateReq
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.IntraStateReq
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class AvailabilityDeliveryRepository(private var serviceApi: ServiceApi, var context: Context?) {

    fun getIntraStatePrice(
        intraStateReq: IntraStateReq?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {

            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject(
                    "breeze/pricing/IntrastateQuote",
                    AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(Gson().toJson(intraStateReq))
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSuccess(Gson().toJson(responce.body()))
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                    "breeze/pricing/IntrastateQuote$intraStateReq",
                                    responce.code(),
                                    responce.message(),
                                    "Price Payment IntraState",
                                    "ErrorCode"
                                )
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show()

                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                "breeze/pricing/IntrastateQuote$intraStateReq",
                                0,
                                e.toString(),
                                "Price Payment IntraState",
                                "OnError"
                            )
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

    fun getInterStatePrice(
        interStateReq: InterStateReq?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {

            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject(
                    "breeze/pricing/InterstateQuote",
                    AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(Gson().toJson(interStateReq))
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSuccess(Gson().toJson(responce.body()))
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                    "breeze/pricing/InterstateQuote$interStateReq",
                                    responce.code(),
                                    responce.message(),
                                    "Price Payment InterState",
                                    "ErrorCode"
                                )
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show()
                            }


                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()

                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                "breeze/pricing/InterstateQuote$interStateReq",
                                0,
                                e.toString(),
                                "Price Payment InterState",
                                "OnError"
                            )

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