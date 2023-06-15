package com.zoom2u_customer.ui.application.bottom_navigation.profile.chnage_password

import android.content.Context
import android.widget.Toast

import com.google.gson.JsonObject
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ChangePassRepository(private var serviceApi: ServiceApi, var context: Context) {


    fun changePass(
        oldPass: String,
        newPass: String,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.postWithJsonObject(
                    "api/account/changePassword?OldPassword=$oldPass&NewPassword=$newPass",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSuccess(responce.body().toString())
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("api/account/changePassword?OldPassword=$oldPass&NewPassword=$newPass",
                                    responce.code(),responce.message(),"Change Password Api","ErrorCode")
                                DialogActivity.alertDialogSingleButton(
                                    context,
                                    "Sorry!",
                                    "May be you entered wrong password, Please try again"
                                )
                            }
                        }

                        override fun onError(e: Throwable) {
                            AppUtility.progressBarDissMiss()

                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("api/account/changePassword?OldPassword=$oldPass&NewPassword=$newPass",
                                0,e.toString(),"Change Password Api","OnError")
                            Toast.makeText(
                                context,
                                R.string.signup_error_msg.toString(),
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