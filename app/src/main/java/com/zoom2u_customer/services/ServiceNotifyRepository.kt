package com.zoom2u_customer.services

import android.util.Log
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ServiceNotifyRepository(private var serviceApi: ServiceApi) {


    fun serviceNotify(courierId:String?, msgTxt:String?,
        disposable: CompositeDisposable = CompositeDisposable(),

    ) {
        if (AppUtility.isInternetConnected()) {
            disposable.add(
                serviceApi.postWithJsonObject(
                    "breeze/admin/NotifyCourierAboutChatMessage?courierId=$courierId&text=$msgTxt",
                  AppUtility.getApiHeaders() ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                Log.d("bxs","josx")
                               // onSuccess(responce.body().toString())

                            } else if (responce.errorBody() != null) {
                                Log.d("bxs","josx")

                            }
                        }

                        override fun onError(e: Throwable) {
                          Log.d("bxs","josx")

                        }
                    })
            )
        } else {
           /* DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )*/
        }
    }
}