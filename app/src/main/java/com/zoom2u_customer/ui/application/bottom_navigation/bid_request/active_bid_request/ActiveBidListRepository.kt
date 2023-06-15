package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request

import android.content.Context
import android.widget.Toast
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ActiveBidListRepository(private var serviceApi: ServiceApi, var context: Context?) {

    fun getActiveBidList(page:Int, disposable: CompositeDisposable = CompositeDisposable(),
                       onSuccess: (String) -> Unit) {
        if (AppUtility.isInternetConnected()) {
            // AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.getWithJsonObject(
                    "breeze/HeavyFreight/GetAllRequests?page=$page&type=active&searchText=",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(responce.body().toString())
                            } else if (responce.errorBody() != null) {
                                onSuccess("false")
                                AppUtility.progressBarDissMiss()
                                if (responce.code() != 401) {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                        "breeze/HeavyFreight/GetAllRequests?page=$page&type=active&searchText=",
                                        responce.code(),
                                        responce.message(),
                                        "ActiveBid List api",
                                        "ErrorCode"
                                    )
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            onSuccess("false")
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/HeavyFreight/GetAllRequests?page=$page&type=active&searchText=",
                                0,e.message,"ActiveBid List api","OnError")
                            Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_SHORT).show()
                        }
                    })
            )
        } else {
            onSuccess("false")
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }


    fun getBidCancel(
        bookingID: Int?,
        pos:String?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.cancelBooking(
                    "breeze/ExtraLargeQuoteRequest/CloseQuoteRequest?requestId=$bookingID",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<Void>>() {
                        override fun onSuccess(responce: Response<Void>) {
                            onSuccess(pos.toString())
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                "Something went wrong please try again.",
                                Toast.LENGTH_SHORT
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


    fun getHeavyBidCancel(
        bookingID: Int?,
        pos:String?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.cancelBooking(
                    "breeze/HeavyFreight/CloseFreightRequest?requestId=$bookingID",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<Void>>() {
                        override fun onSuccess(responce: Response<Void>) {
                            onSuccess(pos.toString())
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                "something went wrong please try again.",
                                Toast.LENGTH_SHORT
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