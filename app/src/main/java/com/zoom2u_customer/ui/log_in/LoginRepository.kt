package com.zoom2u_customer.ui.log_in

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.Response
import java.util.*

class LoginRepository (private var serviceApi: ServiceApi, var context: Context) {
    fun getLoginFromRepo(
        loginRequest: LoginRequest,
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (msg: String,isSuspendedWithUnpaidDues: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            val request: HashMap<String, String> = HashMap<String, String>()
            request["grant_type"] = loginRequest.grant_type
            request["username"] = loginRequest.username
            request["password"] = loginRequest.password
            request["isDeliveriesPortal"] = loginRequest.isDeliveriesPortal

            disposable.add(
                serviceApi.getLoginData(request).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                responce.body()?.addProperty("email",loginRequest.username)
                                AppPreference.getSharedPrefInstance()
                                    .setLoginResponse(Gson().toJson(responce.body()))
                                val jsonObj = JSONObject(responce.body().toString())
                                if (jsonObj.has("isSuspendedWithUnpaidDues")) {
                                    val isSuspendedWithUnpaidDues = jsonObj.optBoolean("isSuspendedWithUnpaidDues", false)
                                   if(isSuspendedWithUnpaidDues) {
                                       onSuccess("true",isSuspendedWithUnpaidDues.toString())
                                   }else {
                                       onSuccess("true",isSuspendedWithUnpaidDues.toString())
                                   }

                                }else {
                                    onSuccess("true","")
                                }
                            } else if (responce.errorBody() != null) {
                                onSuccess("Username password combination is incorrect. Check for spelling errors, spaces in email password, or automatic capitalisation","")
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
        } else {
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }
}