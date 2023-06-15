package com.zoom2u_customer.ui.application.bottom_navigation.history.history_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.ui.application.bottom_navigation.history.HistoryResponse

class HistoryDetailsViewModel :ViewModel() {
    var success: MutableLiveData<HistoryDetailsResponse>?=MutableLiveData()
    private var routeSuccess: MutableLiveData<String> = MutableLiveData("")
    private var cancelSuccess: MutableLiveData<HistoryResponse> = MutableLiveData()
    private var cancelSuccessMAB: MutableLiveData<String> = MutableLiveData("")
    var repository: HistoryDetailsRepository? = null
    var repositoryGoogleAddress: GoogleAddressRepository? = null

    fun setHistoryDetails(bookingRef: String?) =
        repository?.getHistoryDetails(bookingRef, onSuccess = ::onSuccess)

    fun cancelBooking(historyItem: HistoryResponse?) =
        repository?.cancelBooking(historyItem, onSuccess = ::onCancelSuccess)


    fun cancelBooking(historyItem: String?) =
        repository?.cancelBooking(historyItem, onSuccessMAB = ::onCancelFromMABSuccess)

    fun getRoute(url: String?) =
        repositoryGoogleAddress?.getRoute(url,onSuccess = ::onSuccessRoute)


    fun onSuccess(history: HistoryDetailsResponse) {
        success?.value = history

    }

    private fun onCancelSuccess(history:HistoryResponse) {
        cancelSuccess.value = history

    }
    private fun onCancelFromMABSuccess(history:String) {
        cancelSuccessMAB.value = history

    }

    private fun onSuccessRoute(route: String) {
        routeSuccess.value=route
    }

    fun getHistoryDetails(): MutableLiveData<HistoryDetailsResponse>? {
        return success
    }

    fun getRouteSuccess():MutableLiveData<String>{
        return routeSuccess
    }

    fun getCancelBooking():MutableLiveData<HistoryResponse>{
        return cancelSuccess
    }

    fun getCancelBookingMAB():MutableLiveData<String>{
        return cancelSuccessMAB
    }
}