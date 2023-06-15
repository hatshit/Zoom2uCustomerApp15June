package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy

import android.content.Context
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
import org.json.JSONObject
import retrofit2.Response

class UpdateDetailsHeavyRepository (private var serviceApi: ServiceApi, var context: Context?) {
    fun updateDetailsHeavy(
        jObjForPlaceBooking: JSONObject?,
        updateDetailsReq: UpdateDetailsReq,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (success:UpdateDetailsReq) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject(
                    "breeze/HeavyFreight/SaveBidAddressDetails?",
                    AppUtility.getApiHeaders(),
                    JsonParser.parseString(jObjForPlaceBooking.toString()) as JsonObject
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(updateDetailsReq)
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
}