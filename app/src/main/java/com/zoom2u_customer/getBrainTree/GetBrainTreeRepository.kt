package com.zoom2u_customer.getBrainTree

import android.content.Context
import android.widget.Toast
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response

class GetBrainTreeRepository(private var serviceApi: ServiceApi, var context: Context?) {

    fun getBrainTreeToken(
        disposable: CompositeDisposable = CompositeDisposable(),
        onSuccess: (token: String) -> Unit
    ) {
        if (AppUtility.isInternetConnected()) {
            //AppUtility.progressBarShow(context)
            disposable.add(
                serviceApi.brainTreeApiCall(
                    "api/Braintree/BrainTreeToken",
                    AppUtility.getApiHeaders()
                ).subscribeOn(
                    Schedulers.io()
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableSingleObserver<Response<ResponseBody>>() {
                        override fun onSuccess(responce: Response<ResponseBody>) {
                            if ( responce.code()==200 && responce.body() != null) {
                                responce.body()?.string()?.let { onSuccess(it) }

                            } else if (responce.errorBody() != null) {
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
        } else {
            DialogActivity.alertDialogSingleButton(
                context,
                "No Network !",
                "No network connection, Please try again later."
            )
        }
    }
}


