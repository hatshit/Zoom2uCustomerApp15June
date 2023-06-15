package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONObject


class HeavyFreightViewModel: ViewModel() {
    var repository: HeavyFreightRepository? = null
    private var uploadImages: MutableLiveData<MutableList<String>?> = MutableLiveData(null)
    var finalSuccess: MutableLiveData<String> = MutableLiveData()
    fun getQuoteRequest(jObjForPlaceBooking: JSONObject?, arrayImage: MutableList<String>?, itemCategory: String?) = repository?.getQuoteRequest(jObjForPlaceBooking,arrayImage,itemCategory,onSuccess = ::onQuoteSuccess)
    fun uploadQuotesImage(requestId: Int?, heavyFreightQuoteRef: String?, itemCategory: String?, arrayImage: MutableList<String>?)=repository?.uploadQuoteImages(requestId,heavyFreightQuoteRef,arrayImage,itemCategory,onSuccess = ::onUploadImageSuccess)

    private fun onQuoteSuccess(imagePath:MutableList<String>?){
        uploadImages.value =imagePath
    }
    private fun onUploadImageSuccess(success:String){
        finalSuccess.value=success
    }

    fun getQuoteSuccess(): MutableLiveData<MutableList<String>?> {
        return uploadImages
    }

    fun finalSuccess(): MutableLiveData<String> {
        return finalSuccess
    }
}