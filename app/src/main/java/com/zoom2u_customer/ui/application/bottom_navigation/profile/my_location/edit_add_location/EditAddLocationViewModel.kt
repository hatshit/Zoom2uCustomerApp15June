package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.edit_add_location

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.AddLocationReq
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq


class EditAddLocationViewModel : ViewModel() {
    private var editSuccess: MutableLiveData<String>? = MutableLiveData("")
    private var addSuccess: MutableLiveData<String>? = MutableLiveData("")
    private var deleteSuccess: MutableLiveData<String>? = MutableLiveData("")
    private var googleAddUsingAdd : MutableLiveData<HashMap<String, Any>>? = MutableLiveData()
    private var googleAddUsingLatLang: MutableLiveData<HashMap<String, Any>>? = MutableLiveData()
    var repository: EditAddLocationRepository? = null
    var repositoryGoogleAdd : GoogleAddressRepository?=null
    fun editLocation(myLocationResponse: MyLocationResAndEditLocationReq?) =
        repository?.editLocation(myLocationResponse, onSuccess = ::editLocationSuccess)

    fun addLocation(addLocationReq: AddLocationReq?) =
        repository?.addLocation(addLocationReq, onSuccess = ::addLocationSuccess)

    fun deleteLocation(locationId: Int?) =
        repository?.deleteLocation(locationId, onSuccess = ::deleteLocationSuccess)

    fun addFromGoogleAdd(address: String?, isEdit: Boolean) =
        repositoryGoogleAdd?.getAddressFromGeocoder(address,isEdit,onSuccess = ::googleAddUsingAddress)

    fun addFromGoogleLatLang( lat:String?,lang:String?,isPickup: Boolean) =
        repositoryGoogleAdd?.getAddressFromLatLang(lat,lang,isPickup,onSuccess = ::googleAddUsingLatLang)



    private fun editLocationSuccess(msg: String) {
        editSuccess?.value = msg
    }
    private fun addLocationSuccess(msg: String) {
        addSuccess?.value = msg
    }
    private fun deleteLocationSuccess(msg: String) {
        deleteSuccess?.value = msg
    }

    private fun googleAddUsingAddress(address: HashMap<String, Any>){
        googleAddUsingAdd?.value = address
    }

    private fun googleAddUsingLatLang(address: HashMap<String, Any>){
        googleAddUsingLatLang?.value = address
    }

    fun getEditLocationSuccess(): MutableLiveData<String>? {
        return editSuccess
    }

    fun getAddLocationSuccess(): MutableLiveData<String>? {
        return addSuccess
    }

    fun getDeleteLocationSuccess(): MutableLiveData<String>? {
        return deleteSuccess
    }



    fun googleSuccessUsingAdd() : MutableLiveData<HashMap<String, Any>>?{
        return googleAddUsingAdd
    }

    fun googleSuccessUsingLatLang() : MutableLiveData<HashMap<String, Any>>?{
        return googleAddUsingLatLang
    }

}