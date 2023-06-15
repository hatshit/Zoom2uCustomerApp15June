package com.zoom2u_customer.ui.application.bottom_navigation.profile.chnage_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChangePassViewModel : ViewModel() {
    private var success: MutableLiveData<String>? = MutableLiveData("")
    var repository: ChangePassRepository? = null

    fun changePass(oldPass:String,newPass:String) = repository?.changePass(oldPass,newPass,onSuccess = ::onSuccess)

    private fun onSuccess(msg:String){
       success?.value=msg

    }
    fun getChangePassSuccess():MutableLiveData<String>?{
        return success
    }

}