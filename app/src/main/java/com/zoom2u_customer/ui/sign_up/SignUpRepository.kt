package com.zoom2u_customer.ui.sign_up

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.log_in.LoginRequest
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.util.*

class SignUpRepository(
    private var serviceApi: ServiceApi, var context: Context) {

    fun getSignUpFromRepo(signUpRequest: SignUpRequest, disposable: CompositeDisposable = CompositeDisposable(),
                          onSignupSuccess: (msg: String) -> Unit) {
        if (AppUtility.isInternetConnected()) {
            AppUtility.progressBarShow(context)
            val request: HashMap<String, String> = HashMap<String, String>()
            request["firstName"] = signUpRequest.firstName
            request["lastName"] = signUpRequest.lastName
            request["company"] = signUpRequest.company
            request["userName"] = signUpRequest.userName
            request["mobile"] = signUpRequest.mobile
            request["howDidYouFindUs"] = signUpRequest.howDidYouFindUs
            request["password"] = signUpRequest.password
            request["confirmPassword"] = signUpRequest.confirmPassword
            request["terms"] = signUpRequest.terms
            request["courier"] = signUpRequest.courier
            request["customerType"] = signUpRequest.customerType
            disposable.add(
                serviceApi.getSignUpData(request).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null)
                                onSignupSuccess("true")
                            else if (responce.errorBody() != null) {
                                AppUtility.progressBarDissMiss()
                                DialogActivity.alertDialogSingleButton(
                                    context,
                                    "Unsuccessful !",
                                    "This email id is already register use other mail id."
                                )
                            }
                        }
                        override fun onError(e: Throwable) {
                            Log.d("", "")
                            AppUtility.progressBarDissMiss()
                            Toast.makeText(context,R.string.signup_error_msg.toString(),Toast.LENGTH_LONG).show()
                        }


                    })
            )
        }else {
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }



    fun getLoginFromRepo(
        loginRequest: LoginRequest,
        disposable: CompositeDisposable = CompositeDisposable(),
        onLoginSuccess: (msg: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
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
                                AppPreference.getSharedPrefInstance()
                                    .setLoginResponse(Gson().toJson(responce.body()))
                                onLoginSuccess("true")
                            } else if (responce.errorBody() != null) {
                                onLoginSuccess("Username password combination is incorrect. Check for spelling errors, spaces in email password, or automatic capitalisation")
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
