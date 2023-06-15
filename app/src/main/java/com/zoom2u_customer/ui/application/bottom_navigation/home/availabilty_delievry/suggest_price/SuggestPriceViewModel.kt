package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.suggest_price

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class SuggestPriceViewModel: ViewModel() {
    private var intraSuccess: MutableLiveData<String> = MutableLiveData(null)
    private var interSuccess: MutableLiveData<String> = MutableLiveData(null)
    var repository:SuggestPriceRepository? = null

    fun getPriceRequestIntraState(jObjForPlaceBooking: JSONObject?) = repository?.getPriceRequestIntraState(jObjForPlaceBooking,onSuccess = ::onIntraSuccess)
    fun getPriceRequestInterState(jObjForPlaceBooking: JSONObject?) = repository?.getPriceRequestInterState(jObjForPlaceBooking,onSuccess = ::onInterSuccess)



    private fun onIntraSuccess(imagePath:String){
        intraSuccess.value =imagePath
    }

    fun getIntraPriceSuccess(): MutableLiveData<String> {
        return intraSuccess
    }

    private fun onInterSuccess(imagePath:String){
        interSuccess.value =imagePath
    }

    fun getInterPriceSuccess(): MutableLiveData<String> {
        return interSuccess
    }

}