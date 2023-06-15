package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page

import android.content.Context
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class CompletedDetailsRepository(private var serviceApi: ServiceApi, var context: Context?) {

    fun getBidDetails(quoteId:Int?,disposable: CompositeDisposable = CompositeDisposable(),
                       onSuccess: (history: CompletedDetailsResponse) -> Unit) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.getWithJsonObject(
                    "breeze/ExtraLargeQuoteRequest/GetQuoteRequestsDetail?requestId=$quoteId",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                val bid: CompletedDetailsResponse =
                                    Gson().fromJson(responce.body()?.get("data"), CompletedDetailsResponse::class.java)
                                onSuccess(bid)
                            }
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show() }
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/ExtraLargeQuoteRequest/GetQuoteRequestsDetail?requestId=$quoteId",
                                    responce.code(),responce.message(),"CompletedBid Details api","ErrorCode")
                        }



                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/ExtraLargeQuoteRequest/GetQuoteRequestsDetail?requestId=$quoteId",
                                0,e.toString(),"CompletedBid Details api","OnError")

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

    fun getHeavyFreightBidDetails(quoteReference:String?,disposable: CompositeDisposable = CompositeDisposable(),
                                  onSuccess: (history: HeavyFreightBidDetails) -> Unit) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)

            disposable.add(
                serviceApi.getWithJsonObject(
                    "breeze/HeavyFreight/GetFreightRequestDetailsSummaryForCustomer?quoteReference=$quoteReference",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                val bid: HeavyFreightBidDetails =
                                    Gson().fromJson(responce.body()?.get("data"), HeavyFreightBidDetails::class.java)
                                onSuccess(bid)

                            }
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/HeavyFreight/GetFreightRequestDetailsSummaryForCustomer?quoteReference=$quoteReference",
                                    responce.code(),responce.message(),"HeavyFreight ActiveBid Details api","ErrorCode")
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show() }

                        }
                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/HeavyFreight/GetFreightRequestDetailsSummaryForCustomer?quoteReference=$quoteReference",
                                0,e.message,"HeavyFreight ActiveBid Details api","OnError")

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