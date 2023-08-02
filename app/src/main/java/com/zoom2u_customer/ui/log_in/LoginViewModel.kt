package com.zoom2u_customer.ui.log_in

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class LoginViewModel : ViewModel(){
    var success:MutableLiveData<LoginReprocess>? =MutableLiveData()

   var repository: LoginRepository? = null

    fun getLogin(loginRequest: LoginRequest) = repository?.getLoginFromRepo(loginRequest,onSuccess = ::onSuccess)


    fun onSuccess(msg:String,isSuspendedWithUnpaidDues:String){
        success?.value=LoginReprocess(msg,isSuspendedWithUnpaidDues)

    }


    fun getLoginSuccess(): MutableLiveData<LoginReprocess>? {
        return success
    }
}