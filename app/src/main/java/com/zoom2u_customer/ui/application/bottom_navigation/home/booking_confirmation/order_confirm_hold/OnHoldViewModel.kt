package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.history.history_details.HistoryDetailsRepository


class OnHoldViewModel : ViewModel() {
    var repository: OnHoldRepository? = null
    private var activateSuccess: MutableLiveData<String>? = MutableLiveData("")
    var repositoryHistory: HistoryDetailsRepository? = null
    private var cancelSuccess: MutableLiveData<String> = MutableLiveData("")
    fun activateRequest(bookingRef:String?,nonce:String?) = repository?.activateMyBooking(bookingRef,nonce,onSuccess = ::onDeliverySuccess)


    private fun onDeliverySuccess(success : String){
        activateSuccess?.value=success
    }

    fun cancelBooking(historyItem: String?) =
        repositoryHistory?.cancelBooking(historyItem, onSuccessMAB = ::onCancelBooking)


    fun getActivateSuccess(): MutableLiveData<String>?{
        return activateSuccess
    }

    private fun onCancelBooking(history:String) {
        cancelSuccess.value = history

    }

    fun getCancelBooking():MutableLiveData<String>{
        return cancelSuccess
    }
}