package com.zoom2u_customer.ui.log_in.forgot_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class ForgotPassViewModel : ViewModel(){
    var success: MutableLiveData<String>? = MutableLiveData("")
    var repository: ForgotPassRepository? = null

    fun reSetPass(username :String) = repository?.setForgotPass(username,onSuccess = ::onSuccess)

    fun onSuccess(msg:String){
        success?.value=msg

    }


    fun getForgetPassSuccess():MutableLiveData<String>?{
        return success
    }
}