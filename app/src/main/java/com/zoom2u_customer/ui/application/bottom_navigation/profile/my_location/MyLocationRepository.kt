package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MyLocationRepository(private var serviceApi: ServiceApi, var context: Context) {

    fun getMyLocation(
        isFromMAB: Boolean,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (myLocationList: List<MyLocationResAndEditLocationReq>) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            //CustomProgressBar.progressBarShow(context,"Loading...","Please wait a moment")
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.getWithJsonArray(
                    "breeze/customer/GetPreferredLocations",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonArray>>() {
                        override fun onSuccess(responce: Response<JsonArray>) {
                            if (responce.body() != null) {
                                val listType =
                                    object :
                                        TypeToken<List<MyLocationResAndEditLocationReq?>?>() {}.type
                                val list: List<MyLocationResAndEditLocationReq> =
                                    Gson().fromJson(responce.body(), listType)
                                onSuccess(list)
                            } else if (responce.errorBody() != null) {
                                if (isFromMAB)
                                    LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                        "breeze/customer/GetPreferredLocations",
                                        responce.code(),
                                        responce.message(),
                                        "Delivery Details My Location api",
                                        "ErrorCode"
                                    )
                                else
                                    AppUtility.progressBarDissMiss()
                                    LogErrorsToAppCenter().addLogToAppCenterOnAPIFail(
                                        "breeze/customer/GetPreferredLocations",
                                        responce.code(),
                                        responce.message(),
                                        "My location page My Location api",
                                        "ErrorCode"
                                    )

                                Toast.makeText(
                                        context,
                                        "Something went wrong please try again.",
                                        Toast.LENGTH_LONG
                                    ).show()


                            }
                        }

                        private fun onOkClick() {
                            AppUtility.onLogoutCall(context)
                        }


                        override fun onError(e: Throwable) {
                            //CustomProgressBar.dismissProgressBar()

                            AppUtility.progressBarDissMiss()
                            if(isFromMAB)
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/GetPreferredLocations",
                                    0,e.toString(),"Delivery Details My Location api","OnError")
                            else
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/GetPreferredLocations",
                                    0,e.toString(),"My location page My Location api","OnError")


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