package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.braintreepayments.api.DropInClient
import com.braintreepayments.api.DropInListener
import com.braintreepayments.api.DropInRequest
import com.braintreepayments.api.DropInResult
import com.google.gson.Gson
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityBookingConfirmationBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.interstate_booking.InterStateFirstScreen
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OnHoldActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OrderConfirmActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BookingConfirmationActivity : AppCompatActivity(), View.OnClickListener,DropInListener {
    private var bookingDeliveryResponse: JSONObject? = null
    lateinit var binding: ActivityBookingConfirmationBinding
    private lateinit var itemDataList: ArrayList<Icon>
    private lateinit var adapter: BookingPackageAdapter
    lateinit var viewModel: BookingConfirmationViewModel
    private var repository: BookingConfirmationRepository? = null
    var dropInClient : DropInClient?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_confirmation)

        viewModel = ViewModelProvider(this)[BookingConfirmationViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = BookingConfirmationRepository(serviceApi, this)
        viewModel.repository = repository
        /**get data from map Item*/
        if (intent.hasExtra("IconList")) {
            itemDataList = intent.getParcelableArrayListExtra<Icon>("IconList") as ArrayList<Icon>
            bookingDeliveryResponse = JSONObject(intent.getStringExtra("MainJsonForMakeABooking"))
            setDataView(bookingDeliveryResponse)
        }
        setAdapterView()
        binding.bookingConfirmation.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        viewModel.getDeliverySuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                if (it.isNotEmpty()) {
                    val bookingResponse: BookingResponse =
                        Gson().fromJson(it, BookingResponse::class.java)
                    if (!bookingResponse.`$type`
                            .equals("System.Web.Http.HttpError, System.Web.Http", ignoreCase = true)
                    ) {
                        if (bookingResponse.Verified == true) {
                            val loginPage = Intent(this, OrderConfirmActivity::class.java)
                            loginPage.putExtra(
                                "BookingResponse", bookingResponse
                            )
                            intent.flags=Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            startActivity(loginPage)
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
        }




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
        dropInClient = DropInClient(this,AppPreference.getSharedPrefInstance().getBrainToken())
    }




    override fun onDropInSuccess(dropInResult: DropInResult) {
        val paymentMethodNonce = dropInResult.paymentMethodNonce?.string
        try {
            bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")?.remove("PricingScheme")
            bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")
                ?.put("paymentNonce", paymentMethodNonce)
            viewModel.getDeliveryRequest(true,bookingDeliveryResponse)

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

    @SuppressLint("SetTextI18n")
    private fun setDataView(bookingRequest: JSONObject?) {

        binding.time.text = bookingRequest?.getJSONObject("_deliveryRequestModel")?.getString("ETA")
        binding.totalPrice.text =
            "$" + (bookingRequest?.getJSONObject("_deliveryRequestModel")?.getInt("Price") as Int +
                    bookingRequest.getJSONObject("_deliveryRequestModel").getInt("BookingFee"))

        binding.pickName.text =
            bookingRequest.getJSONObject("_deliveryRequestModel").getJSONObject("PickupLocation")
                .getString("ContactName")
        binding.pickAdd.text =
            bookingRequest.getJSONObject("_deliveryRequestModel").getJSONObject("PickupLocation")
                .getString("Address")
        binding.dropName.text =
            bookingRequest.getJSONObject("_deliveryRequestModel").getJSONObject("DropLocation")
                .getString("ContactName")
        binding.dropAdd.text =
            bookingRequest.getJSONObject("_deliveryRequestModel").getJSONObject("DropLocation")
                .getString("Address")

        binding.weight.text =
            bookingRequest.getJSONObject("_deliveryRequestModel").getString("Weight")


    }


    fun setAdapterView() {
        val layoutManager = GridLayoutManager(this, 1)
        binding.iconView.layoutManager = layoutManager
        adapter = BookingPackageAdapter(this, itemDataList)
        binding.iconView.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.booking_confirmation -> {

                if (binding.chkTerms.isChecked) {
                    bookingConfirmation()
                }else{
                    Toast.makeText(this,"Please Accept the customer Terms and Conditions.", Toast.LENGTH_LONG).show()
                }
            }
            R.id.back_btn -> {
                val intent = Intent()
                setResult(3, intent)
                finish()
            }
        }
    }


    private fun callServiceForBookingRequest() {
        try {
            /**check if in delivery details page time not selected from time window*/
            if(bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")?.has("isPickTimeSelectedFromTimeWindow") == true)
                bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")?.remove("isPickTimeSelectedFromTimeWindow")

                if (bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")?.has("ETA") == true)
                bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")?.remove("ETA")

            if (AppPreference.getSharedPrefInstance().getAccountType() == "\"Standard\"") {
                val dropInRequest = DropInRequest()
                dropInClient?.setListener(this)
                dropInClient?.launchDropIn(dropInRequest)
            }  else  {
                AppUtility.progressBarShow(this)
                viewModel.getDeliveryRequest(true,bookingDeliveryResponse)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
/*

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Request_Code) {
            when (resultCode) {
                RESULT_OK -> {
                    val result: DropInResult? =
                        data?.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                    val nonce1:PaymentMethodNonce?= result?.paymentMethodNonce
                    nonce = nonce1?.nonce
                    try {
                        bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")?.remove("PricingScheme")
                        bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")
                            ?.put("paymentNonce", nonce)
                        viewModel.getDeliveryRequest(true,bookingDeliveryResponse)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                RESULT_CANCELED -> {
                    Toast.makeText(this, "Payment initiation failed,please try again.  ", Toast.LENGTH_SHORT).show();
                }
                else -> {
                     val error :Exception= data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
*/

    private fun bookingConfirmation() {

        try {
            if (bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel")
                    .getBoolean("IsInterstate")
            ) {
                if (bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel")
                        .getString("DeliverySpeed") == "Interstate" || bookingDeliveryResponse!!.getJSONObject(
                        "_deliveryRequestModel"
                    ).getString("DeliverySpeed") == "Road interstate"
                ) {

                    if (bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel")
                            .getString("DeliverySpeed") == "Interstate"
                    ) {
                        val intent = Intent(this, InterStateFirstScreen::class.java)
                        intent.putExtra(
                            "MainJsonForMakeABooking",
                            bookingDeliveryResponse.toString()
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)


                    } else if (bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel")
                            .getString("DeliverySpeed") == "Road interstate"
                    ) {
                        //interStateHeaderTxt.setText(R.string.interstateAlertHeaderroadinterstate)
                        //interStateMsgTxt.setText(R.string.interstateMsgTxtroadInterstate)
                    }

                } else
                    callServiceForBookingRequest()
            } else callServiceForBookingRequest()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        val intent = Intent()
        setResult(3, intent)
        finish()
    }



}