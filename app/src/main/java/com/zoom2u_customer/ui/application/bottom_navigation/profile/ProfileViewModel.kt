package com.zoom2u_customer.ui.application.bottom_navigation.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel :ViewModel(){
    var success: MutableLiveData<String> = MutableLiveData("")

    var repository: ProfileRepository? = null

    fun getProfile() = repository?.getProflie(onSuccess = ::onSuccess)

    fun onSuccess(msg:String){
        success.value=msg

    }

    fun getProfileSuccess():MutableLiveData<String>{
        return success
    }
}