package com.zoom2u_customer.ui.application.chat

import android.content.Context
import android.widget.Toast
import com.google.gson.JsonObject
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.LogErrorsToAppCenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class ChatListRepository(private var serviceApi: ServiceApi, var context: Context?) {

    fun getChatList(disposable: CompositeDisposable = CompositeDisposable(),
                       onSuccess: (history:String) -> Unit) {
        if (AppUtility.isInternetConnected()) {
            //AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.getWithJsonObject(
                    "breeze/customer/DeliveriesToChatAbout",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<JsonObject>>() {
                        override fun onSuccess(responce: Response<JsonObject>) {
                            if (responce.body() != null) {
                                onSuccess(responce.body().toString())

                            }
                            else if (responce.errorBody() != null) {
                                onSuccess("false")
                                AppUtility.progressBarDissMiss()

                                if (responce.code() == 401) {
                                    DialogActivity.alertDialogOnSessionExpire(context, onItemClick = ::onOkClick)
                                } else {
                                    Toast.makeText(context, "Something went wrong please try again.", Toast.LENGTH_SHORT).show()
                                    LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/DeliveriesToChatAbout", responce.code(), responce.message(), "Chat List api", "ErrorCode")
                                }
                            }


                        }
                        private fun onOkClick() {
                            AppUtility.onLogoutCall(context)
                        }

                        override fun onError(e: Throwable) {
                            onSuccess("false")
                            AppUtility.progressBarDissMiss()
                            LogErrorsToAppCenter().addLogToAppCenterOnAPIFail("breeze/customer/DeliveriesToChatAbout",
                                0,e.toString(),"Chat List api","OnError")
                            Toast.makeText(
                                context,
                                "something went wrong please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            )
        } else {
            onSuccess("false")
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }
}