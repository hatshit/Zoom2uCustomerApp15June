package com.zoom2u_customer.ui.application.bottom_navigation.base_page

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class BasePageRepository(private var serviceApi: ServiceApi, var context: Context?) {

    fun getAccountType(disposable: CompositeDisposable = CompositeDisposable()) {
        if (AppUtility.isInternetConnected()) {
            disposable.add(
                serviceApi.getWithJsonObject(
                    "breeze/customer/GetCustomerAccountType",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                AppPreference.getSharedPrefInstance()
                                    .setAccountType(responce.body()?.get("accountType").toString())
                            } else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                if (responce.code() != 401) {
                                    Toast.makeText(
                                        context,
                                        "Something went wrong please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                        "breeze/customer/GetCustomerAccountType",
                                        responce.code(),
                                        responce.message(),
                                        "Get Account type",
                                        "ErrorCode"
                                    )
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/GetCustomerAccountType",
                                0,e.message,"Get Account type","OnError")
                            Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_SHORT).show()
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




    fun sendDeviceTokenID(
        lat: String,
        lang: String,
        token: String,
        disposable: CompositeDisposable = CompositeDisposable(),

    ) {
        if (AppUtility.isInternetConnected()) {
            val locationStr: String = "$lat,$lang"
            if (!TextUtils.isEmpty(locationStr)) {
                disposable.add(
                    serviceApi.postWithJsonObject(
                        "breeze/customer/UpdateCustomerDeviceId?deviceId=$token&deviceType=Android&location=$locationStr",
                        AppUtility.getApiHeaders()
                    ).subscribeOn(
                        Schedulers.io()
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                            override fun onSuccess(responce: Response<JsonObject>) {
                                if (responce.body() != null)

                                else if (responce.errorBody() != null) {
                                    AppUtility.progressBarDissMiss()
                                    if (responce.code() != 401)
                                        Toast.makeText(context, "something went wrong please try again.", Toast.LENGTH_LONG).show()
                                }
                            }
                            override fun onError(e: Throwable) {
                                AppUtility.progressBarDissMiss()
                                Toast.makeText(
                                    context,
                                    "something went wrong please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                )
            }

        }
        else {
           /* DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )*/
        }
    }



    fun sendDeviceTokenIDWithOutLocation(
        token: String,
        disposable: CompositeDisposable = CompositeDisposable(),

        ) {
        if (AppUtility.isInternetConnected()) {
            disposable.add(
                    serviceApi.postWithJsonObject(
                        "breeze/customer/UpdateCustomerDeviceId?deviceId=$token&deviceType=Android",
                        AppUtility.getApiHeaders()
                    ).subscribeOn(
                        Schedulers.io()
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                            override fun onSuccess(responce: Response<JsonObject>) {
                                if (responce.body() != null)
/*                                    Toast.makeText(
                                        context,
                                        "Device token send",
                                        Toast.LENGTH_LONG
                                    ).show()*/
                                else if (responce.errorBody() != null) {
                                    AppUtility.progressBarDissMiss()
                                    if (responce.code() != 401)
                                    Toast.makeText(
                                        context,
                                        "something went wrong please try again.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                            override fun onError(e: Throwable) {
                                AppUtility.progressBarDissMiss()
                                Toast.makeText(
                                    context,
                                    "something went wrong please try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                )
            }
        else {
         /*   DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )*/
        }
    }


}