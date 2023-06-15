package com.zoom2u_customer.ui.log_in.forgot_password

import android.content.Context
import android.widget.Toast
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.util.*

class ForgotPassRepository (private var serviceApi: ServiceApi, private var context: Context){

    fun setForgotPass(username :String,
        disposable: CompositeDisposable = CompositeDisposable(),
                      onSuccess: (msg: String) -> Unit) {
        AppUtility.progressBarShow(context)
        val request: HashMap<String, String> = HashMap<String, String>()
        request["username"] = username

        disposable.add(
            serviceApi.reSetPassword("api/account/forgotPassword?username=$username").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                    override fun onSuccess(responce: Response<JsonObject>) {
                        if (responce.body() != null)
                            onSuccess(username)
                        else if(responce.errorBody()!=null){
                            AppUtility.progressBarDissMiss()
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


}