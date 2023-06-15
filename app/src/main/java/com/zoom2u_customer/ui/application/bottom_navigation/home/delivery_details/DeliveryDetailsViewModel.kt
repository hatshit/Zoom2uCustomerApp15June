package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.MyLocationRepository
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq

class DeliveryDetailsViewModel : ViewModel() {
    var bookDeliveryAlertMsgStr = ""
    var success: MutableLiveData<List<MyLocationResAndEditLocationReq>>? = MutableLiveData(null)
    private var googleAddUsingAdd: MutableLiveData<HashMap<String, Any>>? = MutableLiveData()
    private var googleAddUsingLatLang: MutableLiveData<HashMap<String, Any>>? = MutableLiveData()
    var repositoryMyLoc: MyLocationRepository? = null
    var repository: DeliveryDetailsRepository? = null
    var repositoryGoogleAddress: GoogleAddressRepository? = null




    fun getMyLocationList(isFromMAB:Boolean) = repositoryMyLoc?.getMyLocation(isFromMAB,onSuccess = ::onSuccess)

    fun addFromGoogleAdd(address: String?, isPickUp: Boolean) =
        repositoryGoogleAddress?.getAddressFromGeocoder(
            address,
            isPickUp,
            onSuccess = ::googleAddUsingAddress
        )

    fun addFromGoogleLatLang(lat: String?, lang: String?, isPickup: Boolean) =
        repositoryGoogleAddress?.getAddressFromLatLang(
            lat,
            lang,
            isPickup,
            onSuccess = ::googleAddUsingLatLang
        )




    fun onSuccess(myLocationList: List<MyLocationResAndEditLocationReq>) {
        success?.value = myLocationList
    }

    private fun googleAddUsingAddress(address: HashMap<String, Any>) {
        googleAddUsingAdd?.value = address
    }

    private fun googleAddUsingLatLang(address: HashMap<String, Any>) {
        googleAddUsingAdd?.value = address
    }

    fun getMySuccess(): MutableLiveData<List<MyLocationResAndEditLocationReq>>? {
        return success
    }

    fun googleSuccessUsingAdd(): MutableLiveData<HashMap<String, Any>>? {
        return googleAddUsingAdd
    }

    fun googleSuccessUsingLatLang(): MutableLiveData<HashMap<String, Any>>? {
        return googleAddUsingLatLang
    }




}