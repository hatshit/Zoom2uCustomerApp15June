package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActiveBidListViewModel : ViewModel() {

    var success: MutableLiveData<String> = MutableLiveData("")
    private var cancelSuccess:MutableLiveData<String>? = MutableLiveData("")
    var cancelHeavySuccess:MutableLiveData<String>? = MutableLiveData("")
    var repository: ActiveBidListRepository? = null

    fun getActiveBidList(page:Int) = repository?.getActiveBidList(page,onSuccess = ::onSuccess)

  /*  fun getBidCancel(Id:Int?) = repository?.getBidCancel(Id,onSuccess = ::onBidCancelSuccess)

    fun getHeavyBidCancel(Id:Int?) = repository?.getHeavyBidCancel(Id,onSuccess = ::onHeavyBidCancelSuccess)*/

    fun onSuccess(history:String){
        success.value=history

    }

   /* private fun onBidCancelSuccess(success:String){
        cancelSuccess?.value=success
    }

    private fun onHeavyBidCancelSuccess(success:String){
        cancelHeavySuccess?.value=success
    }*/

    fun getActiveBidListSuccess(): MutableLiveData<String>{
        return success
    }

 /*   fun getBidCancelSuccess():MutableLiveData<String>?{
        return cancelSuccess
    }

    fun getHeavyBidCancelSuccess():MutableLiveData<String>?{
        return cancelHeavySuccess
    }*/
}