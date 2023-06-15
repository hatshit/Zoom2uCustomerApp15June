package com.zoom2u_customer.ui.application.bottom_navigation.base_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileRepository

class BasePageViewModel : ViewModel(){

    var success: MutableLiveData<String>? = MutableLiveData("")
    var userDataSuccess: MutableLiveData<String>? = MutableLiveData("")
    var profileRepository: ProfileRepository? = null

    var repository: BasePageRepository? = null

    fun getAccountType()=repository?.getAccountType()

    fun sendDeviceTokenID(lat: Double, lang: Double, token:String) = repository?.sendDeviceTokenID(lat.toString(),lang.toString(),token)

    fun sendDeviceTokenIDWithOutLocation(token:String) = repository?.sendDeviceTokenIDWithOutLocation(token)

    fun getProfile() = profileRepository?.getProflie(onSuccess = ::onSuccess)

    fun onSuccess(msg:String){
        userDataSuccess?.value=msg

    }





}