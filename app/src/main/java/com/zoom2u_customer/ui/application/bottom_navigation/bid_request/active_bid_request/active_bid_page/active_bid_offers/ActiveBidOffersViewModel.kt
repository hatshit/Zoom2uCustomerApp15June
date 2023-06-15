package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer


class ActiveBidOffersViewModel : ViewModel() {
    private var success: MutableLiveData<String>? = MutableLiveData("")
    var repository: ActiveBidOffersRepository? = null


    fun quotePayment(nonce:String,requestId:String,offerId: String,orderNo:String) = repository?.quotePayment(nonce,requestId,offerId,orderNo,onSuccess = ::onSuccess)

    fun heavyQuotePayment(nonce:String,requestId:String,
                          offerId: String,carrierPrice: String,
                          customerPrice: String,
                          isPayByInvoiceMarked: Boolean,
                          orderNo:String) = repository?.heavyQuotePayment(nonce,requestId,offerId,carrierPrice,customerPrice,isPayByInvoiceMarked,orderNo,onSuccess = ::onSuccess)


    fun heavyQuoteInvoicePayment(nonce:String,requestId:String,
                          offerId: String,carrierPrice: String,
                          customerPrice: String,
                          isPayByInvoiceMarked: Boolean,
                          orderNo:String) = repository?.heavyQuoteInvoicePayment(nonce,requestId,offerId,carrierPrice,customerPrice,isPayByInvoiceMarked,orderNo,onSuccess = ::onSuccess)

  fun rejectOffer(offer: Offer, onItemRemoved: (Offer) -> Unit){
      repository?.rejectBidOffer(offer,onItemRemoved)
  }




    private fun onSuccess(msg:String){
        success?.value=msg

    }


    fun getQuotePayment(): MutableLiveData<String>?{
        return success
    }

    fun getHeavyQuotePayment(): MutableLiveData<String>?{
        return success
    }

    fun getHeavyQuoteInvoicePayment(): MutableLiveData<String>?{
        return success
    }
}