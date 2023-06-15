package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq


class MyLocationViewModel :ViewModel() {
    var success: MutableLiveData<List<MyLocationResAndEditLocationReq>>? = MutableLiveData(null)

    var repository: MyLocationRepository? = null

    fun getMyLocation(isFromMAB:Boolean) = repository?.getMyLocation(isFromMAB,onSuccess = ::onSuccess)

    fun onSuccess(myLocationList:List<MyLocationResAndEditLocationReq>){
        success?.value=myLocationList
    }

    fun getMySuccess():MutableLiveData<List<MyLocationResAndEditLocationReq>>?{
        return success
    }

}