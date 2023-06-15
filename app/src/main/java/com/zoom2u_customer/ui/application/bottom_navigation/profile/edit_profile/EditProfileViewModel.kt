package com.zoom2u_customer.ui.application.bottom_navigation.profile.edit_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileResponse

class EditProfileViewModel : ViewModel(){
    var success: MutableLiveData<String>? = MutableLiveData("")
    var dpSuccess: MutableLiveData<String>? = MutableLiveData("")
    var repository: EditProfileRepository? = null

    fun setProfile(profileResponse: ProfileResponse?) = repository?.setProfile(profileResponse,onSuccess = ::onSuccess)

   fun uploadDp(path: String?)= repository?.changeDp(path ,onSuccess = ::onDpSuccess)

    fun onSuccess(msg: String){
        success?.value=msg

    }
    private fun onDpSuccess(msg: String){
        dpSuccess?.value=msg

    }

    fun editProfileSuccess(): MutableLiveData<String>?{
        return success
    }

    fun editDpSuccess(): MutableLiveData<String>?{
        return success
    }
}