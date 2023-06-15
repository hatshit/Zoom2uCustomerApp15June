package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject

class UploadQuotesViewModel : ViewModel() {
    var repository: UploadQuotesRepository? = null
    private var uploadImages: MutableLiveData<MutableList<String>?> = MutableLiveData(null)
    var finalSuccess: MutableLiveData<String> = MutableLiveData()
    fun getQuoteRequest(jObjForPlaceBooking: JSONObject?, arrayImage:MutableList<String>?) = repository?.getQuoteRequest(jObjForPlaceBooking,arrayImage,onSuccess = ::onQuoteSuccess)
    fun uploadQuotesImage(requestId:Int?, arrayImage:MutableList<String>?)=repository?.uploadQuoteImages(requestId,arrayImage,onSuccess = ::onUploadImageSuccess)

    private fun onQuoteSuccess(imagePath:MutableList<String>?){
        uploadImages.value =imagePath
    }
    private fun onUploadImageSuccess(success:String){
      finalSuccess.value=success
    }

    fun getQuoteSuccess(): MutableLiveData<MutableList<String>?> {
        return uploadImages
    }

    fun finalSuccess():MutableLiveData<String>{
        return finalSuccess
    }
}