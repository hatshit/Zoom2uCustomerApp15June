package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class UpdateDetailsHeavyViewModel: ViewModel() {
    private var success: MutableLiveData<UpdateDetailsReq>? = MutableLiveData(null)
    var repository: UpdateDetailsHeavyRepository? = null

    fun updateDetailsHeavy(jObjForPlaceBooking: JSONObject?,updateDetailsReq: UpdateDetailsReq) = repository?.updateDetailsHeavy(jObjForPlaceBooking,updateDetailsReq,onSuccess = ::onSuccess)


    private fun onSuccess(msg:UpdateDetailsReq){
        success?.value=msg
    }


    fun getUpdateDetailsSuccess(): MutableLiveData<UpdateDetailsReq>?{
        return success
    }


}