package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails

class CompletedDetailsViewModel :ViewModel() {
    var success: MutableLiveData<CompletedDetailsResponse>?=MutableLiveData()
    private var routeSuccess: MutableLiveData<String> = MutableLiveData("")
    var repository: CompletedDetailsRepository? = null
    var repositoryGoogleAddress: GoogleAddressRepository? = null
    var heavyFreightSuccess: MutableLiveData<HeavyFreightBidDetails>?=MutableLiveData()

    fun getBidDetails(quoteId: Int?) =
        repository?.getBidDetails(quoteId, onSuccess = ::onBidDetailsSuccess)

    fun getHeavyFreightBidDetails(quoteReference: String?) =
        repository?.getHeavyFreightBidDetails(quoteReference, onSuccess = ::onHeavyFreightBidDetailsSuccess)


    private fun onHeavyFreightBidDetailsSuccess(bid: HeavyFreightBidDetails) {
        heavyFreightSuccess?.value = bid
    }

    fun getRoute(url: String?) =
        repositoryGoogleAddress?.getRoute(url,onSuccess = ::onSuccessRoute)


    fun onBidDetailsSuccess(bid: CompletedDetailsResponse) {
        success?.value = bid

    }

    fun getHeavyFreightBidDetailsSuccess(): MutableLiveData<HeavyFreightBidDetails>? {
        return heavyFreightSuccess
    }

    private fun onSuccessRoute(route: String) {
        routeSuccess.value=route
    }

    fun getBidDetailsSuccess(): MutableLiveData<CompletedDetailsResponse>? {
        return success
    }

    fun getRouteSuccess():MutableLiveData<String>{
        return routeSuccess
    }


}