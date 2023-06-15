package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class BookingConfirmationViewModel : ViewModel() {
    var repository: BookingConfirmationRepository? = null
    private var deliveryRequestSuccess: MutableLiveData<String>? = MutableLiveData("")


    fun getDeliveryRequest(isIntraStateBooking:Boolean,jObjForPlaceBooking: JSONObject?) = repository?.getSaveDeliveryRequest(isIntraStateBooking,jObjForPlaceBooking,onSuccess = ::onDeliverySuccess)


    private fun onDeliverySuccess(success : String){
        deliveryRequestSuccess?.value=success
    }

    fun getDeliverySuccess():MutableLiveData<String>?{
        return deliveryRequestSuccess
    }
}