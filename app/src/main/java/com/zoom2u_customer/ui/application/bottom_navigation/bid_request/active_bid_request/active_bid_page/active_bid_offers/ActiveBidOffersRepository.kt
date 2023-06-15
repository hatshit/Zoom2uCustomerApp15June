package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ActiveBidOffersRepository(private var serviceApi: ServiceApi, var context: Context?) {


    fun quotePayment(
        nonce: String,
        requestId: String,
        offerId: String,
        orderNo:String,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postWithJsonObject(
                    "breeze/ExtraLargeQuoteRequest/AcceptQuoteOffer?requestId=$requestId&offerId=$offerId&paymentNonce=$nonce&purchaseOrderNumber=$orderNo",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSuccess(Gson().toJson(responce.body()))
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                    "breeze/ExtraLargeQuoteRequest/AcceptQuoteOffer?requestId=$requestId&offerId=$offerId&paymentNonce=$nonce&purchaseOrderNumber=$orderNo",
                                    responce.code(), responce.message(), "ActiveBid List api", "BidError"
                                )
                                Toast.makeText(
                                    context,
                                    R.string.signup_error_msg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                R.string.signup_error_msg,
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

    fun heavyQuotePayment(
        nonce: String,
        requestId: String,
        offerId: String,
        carrierPrice: String,
        customerPrice: String,
        isPayByInvoiceMarked: Boolean,
        orderNo: String,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postWithJsonObject(
                    "breeze/HeavyFreight/ConvertBooking?requestId=$requestId&offerId=$offerId&carrierPrice=$carrierPrice&customerPrice=$customerPrice&isPayByInvoiceMarked=$isPayByInvoiceMarked&paymentNonce=$nonce",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSuccess(Gson().toJson(responce.body()))
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                    "breeze/ExtraLargeQuoteRequest/AcceptQuoteOffer?requestId=$requestId&offerId=$offerId&paymentNonce=$nonce&purchaseOrderNumber=$orderNo",
                                    responce.code(), responce.message(), "ActiveBid List api", "BidError"
                                )
                                Toast.makeText(
                                    context,
                                    R.string.signup_error_msg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                R.string.signup_error_msg,
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


    fun heavyQuoteInvoicePayment(
        nonce: String,
        requestId: String,
        offerId: String,
        carrierPrice: String,
        customerPrice: String,
        isPayByInvoiceMarked: Boolean,
        orderNo: String,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postWithJsonObject(
                    "breeze/HeavyFreight/ConvertBooking?requestId=$requestId&offerId=$offerId&carrierPrice=$carrierPrice&customerPrice=$customerPrice&isPayByInvoiceMarked=$isPayByInvoiceMarked&paymentNonce=$nonce",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSuccess(Gson().toJson(responce.body()))
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                    "breeze/ExtraLargeQuoteRequest/AcceptQuoteOffer?requestId=$requestId&offerId=$offerId&paymentNonce=$nonce&purchaseOrderNumber=$orderNo",
                                    responce.code(), responce.message(), "ActiveBid List api", "BidError"
                                )
                                Toast.makeText(
                                    context,
                                    R.string.signup_error_msg,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                R.string.signup_error_msg,
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

    fun rejectBidOffer(
        offer: Offer,
        onItemRemoved: (Offer) -> Unit,
        disposable: CompositeDisposable = CompositeDisposable(),
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postWithJsonObject(
                    "breeze/Customer/RejectBid?offerId=${offer.OfferId}",
                    AppUtility.getApiHeaders())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            AppUtility.progressBarDissMiss()
                            if (responce.body() != null)
                            {
                                val success = responce.body()?.get("success")?.asBoolean
                                if (success!=null&&success) {
                                    onItemRemoved(offer)
                                    Toast.makeText(
                                        context,
                                        "Bid rejected",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                    "breeze/Customer/RejectBid?offerId=${offer.OfferId}",
                                    responce.code(), responce.message(), "ActiveBid List api", "BidError"
                                )
                               val errorMessage= responce.errorBody()?.string()
                                Toast.makeText(
                                    context,
                                    "${responce.code()} : $errorMessage",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(
                                context,
                                R.string.signup_error_msg,
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