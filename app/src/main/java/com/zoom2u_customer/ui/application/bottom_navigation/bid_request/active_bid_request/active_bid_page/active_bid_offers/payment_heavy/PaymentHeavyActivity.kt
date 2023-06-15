package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.payment_heavy

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.braintreepayments.api.DropInClient
import com.braintreepayments.api.DropInListener
import com.braintreepayments.api.DropInRequest
import com.braintreepayments.api.DropInResult

import com.google.gson.Gson
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityPaymentHeavyBinding
import com.zoom2u_customer.databinding.FragmentBidOffersBinding
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.ActiveBidOffersRepository
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.ActiveBidOffersViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy.UpdateDetailsReq
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingResponse
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OnHoldActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OrderConfirmActivity
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import org.json.JSONException

class PaymentHeavyActivity : AppCompatActivity(),DropInListener {
    lateinit var binding: ActivityPaymentHeavyBinding
    private var updateDetailsReq: UpdateDetailsReq? = null
    lateinit var viewModel: ActiveBidOffersViewModel
    private var repository: ActiveBidOffersRepository? = null
    var dropInClient : DropInClient?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_heavy)
        if (intent.hasExtra("PaymentHeavyActivity")) {
            updateDetailsReq = intent.getParcelableExtra("PaymentHeavyActivity")
            setDataToView()
        }
        dropInClient = DropInClient(this,AppPreference.getSharedPrefInstance().getBrainToken())
        viewModel = ViewModelProvider(this)[ActiveBidOffersViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = ActiveBidOffersRepository(serviceApi, this)
        viewModel.repository = repository
        val text =  getString(R.string.term_con1)
        val spannableString = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=resources.getColor(R.color.base_color)
                ds.underlineColor=resources.getColor(R.color.base_color)
                ds.isUnderlineText = true
            }

            override fun onClick(p0: View) {
                try {
                    val browserIntent: Intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.zoom2u.com.au/customer-terms/")
                    )
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        spannableString.setSpan(clickableSpan, 22, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.termsCon.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.termsCon.movementMethod = LinkMovementMethod.getInstance()


        binding.zoom2uHeader.backBtn.setOnClickListener{
           updateDetails()
        }



        binding.nextBtn.setOnClickListener {
            if (binding.chkTerms.isChecked) {
                /**check account type for payment*/
                if (AppPreference.getSharedPrefInstance().getAccountType() == "\"Standard\"") {
                    val dropInRequest = DropInRequest()
                    dropInClient?.setListener(this)
                    dropInClient?.launchDropIn(dropInRequest)
                } else {
                    viewModel.heavyQuotePayment(
                        "",
                        updateDetailsReq?.requestId.toString(),
                        updateDetailsReq?.offerId.toString(),
                        updateDetailsReq?.carrierPrice.toString(),
                        updateDetailsReq?.customerPrice.toString(),
                        false,
                        updateDetailsReq?.orderNo.toString()
                    )
                }
            } else
                Toast.makeText(
                    this,
                    "Please Accept the customer Terms and Conditions.",
                    Toast.LENGTH_LONG
                ).show()
        }
        binding.payByInvoice.setOnClickListener {
         /*   viewModel.heavyQuoteInvoicePayment(
                "",
                updateDetailsReq?.requestId.toString(),
                updateDetailsReq?.offerId.toString(),
                updateDetailsReq?.carrierPrice.toString(),
                updateDetailsReq?.customerPrice.toString(),
                true,
                updateDetailsReq?.orderNo.toString()
            )*/

        }

        viewModel.getHeavyQuotePayment()?.observe(this) {
            if (!it.isNullOrEmpty()) {
                AppUtility.progressBarDissMiss()
                val bookingResponse: BookingResponse =
                    Gson().fromJson(it, BookingResponse::class.java)
                if (!bookingResponse.`$type`
                        .equals("System.Web.Http.HttpError, System.Web.Http", ignoreCase = true)
                ) {
                    if (bookingResponse.Verified == true) {
                        val intent = Intent(this, OrderConfirmActivity::class.java)
                        intent.putExtra("BookingRefFromBid", bookingResponse.BookingRef)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(intent)
                        finish()
                    } else {
                        val intentOnHold = Intent(this, OnHoldActivity::class.java)
                        intentOnHold.putExtra("BookingResponse", bookingResponse)
                        intentOnHold.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intentOnHold)
                        finish()
                    }
                }
            }
        }

        viewModel.getHeavyQuoteInvoicePayment()?.observe(this) {
            if (!it.isNullOrEmpty()) {
                AppUtility.progressBarDissMiss()
                val bookingResponse: BookingResponse =
                    Gson().fromJson(it, BookingResponse::class.java)
                if (!bookingResponse.`$type`
                        .equals("System.Web.Http.HttpError, System.Web.Http", ignoreCase = true)
                ) {
                    if (bookingResponse.Verified == false) {
                        val intent = Intent(this, OrderConfirmActivity::class.java)
                        intent.putExtra("BookingRefFromBid", bookingResponse.BookingRef)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(intent)

                    }
                }
            }
        }
    }

    private fun updateDetails() {
        val intent1 = Intent("bid_refresh1")
        LocalBroadcastManager.getInstance(this@PaymentHeavyActivity).sendBroadcast(intent1)

        val intent = Intent(this, BasePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        updateDetails()
    }


   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request_Code) {
            when (resultCode) {
             *//*   RESULT_OK -> {
                    val result: DropInResult? =
                        data?.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                    val nonce1: PaymentMethodNonce? = result?.paymentMethodNonce
                    val nonce = nonce1?.nonce
                    try {
                        viewModel.heavyQuotePayment(
                            nonce.toString(),
                            updateDetailsReq?.requestId.toString(),
                            updateDetailsReq?.offerId.toString(),
                            updateDetailsReq?.carrierPrice.toString(),
                            updateDetailsReq?.customerPrice.toString(),
                            false,
                            updateDetailsReq?.orderNo.toString()
                        )

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                RESULT_CANCELED -> {
                    Toast.makeText(
                        this,
                        "Payment initiation failed, please try again.",
                        Toast.LENGTH_SHORT
                    ).show();
                }
                else -> {
                    val error: Exception =
                        data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                }*//*
            }
        }
    }
*/
    private fun setDataToView() {
        binding.finalPrice.text = "$"+updateDetailsReq?.customerPrice.toString()
    }

    override fun onDropInSuccess(dropInResult: DropInResult) {
        val paymentMethodNonce = dropInResult.paymentMethodNonce?.string
        try {
            viewModel.heavyQuotePayment(
                paymentMethodNonce.toString(),
                updateDetailsReq?.requestId.toString(),
                updateDetailsReq?.offerId.toString(),
                updateDetailsReq?.carrierPrice.toString(),
                updateDetailsReq?.customerPrice.toString(),
                false,
                updateDetailsReq?.orderNo.toString()
            )

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onDropInFailure(error: java.lang.Exception) {
        Toast.makeText(
            this,
            "Payment initiation failed, please try again.",
            Toast.LENGTH_SHORT
        ).show();
    }
}