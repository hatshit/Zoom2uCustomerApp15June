package com.zoom2u_customer.ui.application.bottom_navigation.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel: ViewModel() {

    var success:MutableLiveData<String> =MutableLiveData("")
    var repository: HistoryRepository? = null

    fun getHistory(page:Int) = repository?.getHistoryList(page,onSuccess = ::onSuccess)

    fun onSuccess(history:String){
        success.value=history

    }


    fun getHistoryList(): MutableLiveData<String>{
        return success
    }
}