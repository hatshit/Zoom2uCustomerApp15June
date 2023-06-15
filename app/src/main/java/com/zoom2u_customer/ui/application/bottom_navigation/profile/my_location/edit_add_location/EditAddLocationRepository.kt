package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.edit_add_location

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.AddLocationReq
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class EditAddLocationRepository(
    private var serviceApi: ServiceApi,
    var context: Context
) {

    fun editLocation(
        myLocationResponse: MyLocationResAndEditLocationReq?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {

            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject(
                    "breeze/customer/SavePreferredLocations",
                    AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(Gson().toJson(myLocationResponse))
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(Gson().toJson(responce.body()))
                            } else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/SavePreferredLocations$myLocationResponse",
                                    responce.code(),responce.message(),"Edit my location Api","OnError")
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show() }


                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/SavePreferredLocations$myLocationResponse",
                                0,e.toString(),"Edit my location Api","OnError")

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

    fun addLocation(
        addLocationReq: AddLocationReq?, disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {

        if (AppUtility.isInternetConnected()) {

            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject(
                    "breeze/customer/SavePreferredLocations",
                    AppUtility.getApiHeaders(),
                    AppUtility.getJsonObject(Gson().toJson(addLocationReq))
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(Gson().toJson(responce.body()))
                            } else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/SavePreferredLocations$addLocationReq",
                                    responce.code(),responce.message(),"Add my location Api","ErrorCode")
                                Toast.makeText(
                                    context,
                                    "something went wrong please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/SavePreferredLocations$addLocationReq",
                                0,e.toString(),"Add my location Api","OnError")
                            Toast.makeText(context, "something went wrong please try again.", Toast.LENGTH_LONG).show()
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

    fun deleteLocation(
        locationId: Int?, disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {

        if (AppUtility.isInternetConnected()) {

            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postWithJsonObject(
                    "breeze/customer/DeleteCustomerPreferredLocation?locationId=$locationId",
                    AppUtility.getApiHeaders()
                )
                    .subscribeOn(
                        Schedulers.io()
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(Gson().toJson(responce.body()))
                            } else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/DeleteCustomerPreferredLocation?locationId=$locationId",
                                    responce.code(),responce.message(),"delete my location Api","ErrorCode")
                                Toast.makeText(context, "something went wrong please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/DeleteCustomerPreferredLocation?locationId=$locationId",
                                0,e.toString(),"delete my location Api","OnError")
                            Toast.makeText(context, "something went wrong please try again.", Toast.LENGTH_LONG).show()
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