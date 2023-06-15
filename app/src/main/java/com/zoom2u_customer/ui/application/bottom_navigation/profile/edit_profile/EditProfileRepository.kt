package com.zoom2u_customer.ui.application.bottom_navigation.profile.edit_profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileResponse
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class EditProfileRepository (private var serviceApi: ServiceApi, var context: Context?){

    fun setProfile(profileResponse: ProfileResponse?,
                   disposable: CompositeDisposable = CompositeDisposable(),
                   onSuccess: (msg :String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postBodyJsonObject("breeze/customer/UpdateCustomer", AppUtility.getApiHeaders(),AppUtility.getJsonObject(Gson().toJson(profileResponse)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(Gson().toJson(responce.body()))
                            }  else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_LONG).show()
                                   LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/UpdateCustomer$profileResponse",
                                    responce.code(),responce.message(),"Edit Profile Api","ErrorCode")
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/UpdateCustomer$profileResponse",
                                0,e.toString(),"Edit Profile Api","OnError")
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

    fun changeDp(
        path:String?,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            val file: File = File(path)
            val requestFile: RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body: MultipartBody.Part=
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            val photo:RequestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), "Photo")
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.changeDp(
                    "api/upload",
                    AppUtility.getApiHeaders(),
                    body,
                    photo
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess((responce.body()?.get("image") as JsonArray).get(0).asString)
                            } else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail( "api/upload",
                                    responce.code(),responce.message(),"Change dp api","")
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