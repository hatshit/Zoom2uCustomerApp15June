package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.interstate_booking

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
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
import com.zoom2u_customer.databinding.ActivityInterStateSecondBinding

import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingConfirmationRepository
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingConfirmationViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingResponse
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OnHoldActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OrderConfirmActivity
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONException
import org.json.JSONObject

class InterStateSecondActivity : AppCompatActivity(), View.OnClickListener,DropInListener {
    lateinit var binding: ActivityInterStateSecondBinding
    private var repository: BookingConfirmationRepository? = null
    lateinit var viewModel: BookingConfirmationViewModel
    var dropInClient : DropInClient?=null
    private var bookingDeliveryResponse: JSONObject? = null
    private var isFName: Boolean? = null
    private var isLName: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_inter_state_second)

        if (intent.hasExtra("MainJsonForMakeABooking")) {
            bookingDeliveryResponse = JSONObject(intent.getStringExtra("MainJsonForMakeABooking"))
        }
        viewModel = ViewModelProvider(this).get(BookingConfirmationViewModel::class.java)
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = BookingConfirmationRepository(serviceApi, this)
        viewModel.repository = repository
        dropInClient = DropInClient(this,AppPreference.getSharedPrefInstance().getBrainToken())
        setAdapterView(binding)
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
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(loginPage)
                            finish()
                        } else {
                            val intentOnHold = Intent(this, OnHoldActivity::class.java)
                            intentOnHold.putExtra("BookingResponse", bookingResponse)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intentOnHold)
                            finish()
                        }
                    }
                }
            }
        }

        binding.chkTerms.setOnClickListener(this)
        binding.chkTerms1.setOnClickListener(this)
        binding.chkTerms2.setOnClickListener(this)
        binding.chkTerms3.setOnClickListener(this)
        binding.chkTerms4.setOnClickListener(this)
        binding.chkTerms5.setOnClickListener(this)
        binding.acceptBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)

        binding.fName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    isFName = true
                    enableAcceptBtn(
                        isFName,
                        isLName,
                        binding.chkTerms.isChecked,
                        binding.chkTerms1.isChecked,
                        binding.chkTerms2.isChecked,
                        binding.chkTerms3.isChecked,
                        binding.chkTerms4.isChecked,
                        binding.chkTerms5.isChecked
                    )
                } else {
                    isFName = false
                    enableAcceptBtn(
                        isFName,
                        isLName,
                        binding.chkTerms.isChecked,
                        binding.chkTerms1.isChecked,
                        binding.chkTerms2.isChecked,
                        binding.chkTerms3.isChecked,
                        binding.chkTerms4.isChecked,
                        binding.chkTerms5.isChecked
                    )
                }
            }
        })

        binding.lName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    isLName = true
                    enableAcceptBtn(
                        isFName,
                        isLName,
                        binding.chkTerms.isChecked,
                        binding.chkTerms1.isChecked,
                        binding.chkTerms2.isChecked,
                        binding.chkTerms3.isChecked,
                        binding.chkTerms4.isChecked,
                        binding.chkTerms5.isChecked
                    )
                } else {
                    isLName = false
                    enableAcceptBtn(
                        isFName,
                        isLName,
                        binding.chkTerms.isChecked,
                        binding.chkTerms1.isChecked,
                        binding.chkTerms2.isChecked,
                        binding.chkTerms3.isChecked,
                        binding.chkTerms4.isChecked,
                        binding.chkTerms5.isChecked
                    )
                }
            }
        })
    }

    fun setAdapterView(binding: ActivityInterStateSecondBinding) {
        val layoutManager = GridLayoutManager(this, 3)
        binding.dangerView.layoutManager = layoutManager
        val adapter = DangerIconAdapter(this, DangerIconDataProvider.iconList)
        binding.dangerView.adapter = adapter

    }

    override fun onDropInSuccess(dropInResult: DropInResult) {
        val paymentMethodNonce = dropInResult.paymentMethodNonce?.string
        try {
            bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")
                ?.remove("PricingScheme")
            bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")
                ?.put("paymentNonce", paymentMethodNonce)
            viewModel.getDeliveryRequest(false, bookingDeliveryResponse)
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

    private fun callServiceForBookingRequest() {
        try {

            /**check if in delivery details page time not selected from time window*/
            if(bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel").has("isPickTimeSelectedFromTimeWindow"))
                bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel").remove("isPickTimeSelectedFromTimeWindow")
            if (bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel").has("ETA"))
                bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel").remove("ETA")
            bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel").put(
                    "DeclarationSignature", binding.fName.text.toString().trim() + " " +
                            binding.lName.text.toString().trim()
                )

            if ( AppPreference.getSharedPrefInstance().getAccountType() == "\"Standard\"") {
                val dropInRequest = DropInRequest()
                dropInClient?.setListener(this)
                dropInClient?.launchDropIn(dropInRequest)
            }else  {
                AppUtility.progressBarShow(this)
                viewModel.getDeliveryRequest(false,bookingDeliveryResponse)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

  /*  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Request_Code) {
            *//*when (resultCode) {
                RESULT_OK -> {
                    val result: DropInResult? =
                        data?.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                    val nonce1: PaymentMethodNonce? = result?.paymentMethodNonce
                    val nonce = nonce1?.nonce
                    try {
                        bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")
                            ?.remove("PricingScheme")
                        bookingDeliveryResponse?.getJSONObject("_deliveryRequestModel")
                            ?.put("paymentNonce", nonce)
                        viewModel.getDeliveryRequest(false, bookingDeliveryResponse)
                        getBrainTreeClientToken = null
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                RESULT_CANCELED -> {
                    Toast.makeText(
                        this,
                        "Payment initiation failed, please try again.  ",
                        Toast.LENGTH_SHORT
                    ).show();
                }
                else -> {
                    val error: Exception =
                        data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }*//*
        }
    }
*/
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.acceptBtn -> {
                if (enableAcceptBtn(
                        isFName,
                        isLName,
                        binding.chkTerms.isChecked,
                        binding.chkTerms1.isChecked,
                        binding.chkTerms2.isChecked,
                        binding.chkTerms3.isChecked,
                        binding.chkTerms4.isChecked,
                        binding.chkTerms5.isChecked
                    )
                ) {
                    callServiceForBookingRequest()
                }else{
                    DialogActivity.alertDialogSingleButton(this, "Oops!", "Please fill out all mandatory fields marked in red.")
                }
            }
            R.id.chk_terms -> {
                enableAcceptBtn(
                    isFName,
                    isLName,
                    binding.chkTerms.isChecked,
                    binding.chkTerms1.isChecked,
                    binding.chkTerms2.isChecked,
                    binding.chkTerms3.isChecked,
                    binding.chkTerms4.isChecked,
                    binding.chkTerms5.isChecked
                )
            }
            R.id.chk_terms1 -> {
                enableAcceptBtn(
                    isFName,
                    isLName,
                    binding.chkTerms.isChecked,
                    binding.chkTerms1.isChecked,
                    binding.chkTerms2.isChecked,
                    binding.chkTerms3.isChecked,
                    binding.chkTerms4.isChecked,
                    binding.chkTerms5.isChecked
                )
            }
            R.id.chk_terms2 -> {
                enableAcceptBtn(
                    isFName,
                    isLName,
                    binding.chkTerms.isChecked,
                    binding.chkTerms1.isChecked,
                    binding.chkTerms2.isChecked,
                    binding.chkTerms3.isChecked,
                    binding.chkTerms4.isChecked,
                    binding.chkTerms5.isChecked
                )
            }
            R.id.chk_terms3 -> {
                enableAcceptBtn(
                    isFName,
                    isLName,
                    binding.chkTerms.isChecked,
                    binding.chkTerms1.isChecked,
                    binding.chkTerms2.isChecked,
                    binding.chkTerms3.isChecked,
                    binding.chkTerms4.isChecked,
                    binding.chkTerms5.isChecked
                )
            }
            R.id.chk_terms4 -> {
                enableAcceptBtn(
                    isFName,
                    isLName,
                    binding.chkTerms.isChecked,
                    binding.chkTerms1.isChecked,
                    binding.chkTerms2.isChecked,
                    binding.chkTerms3.isChecked,
                    binding.chkTerms4.isChecked,
                    binding.chkTerms5.isChecked
                )
            }
            R.id.chk_terms5 -> {
                enableAcceptBtn(
                    isFName,
                    isLName,
                    binding.chkTerms.isChecked,
                    binding.chkTerms1.isChecked,
                    binding.chkTerms2.isChecked,
                    binding.chkTerms3.isChecked,
                    binding.chkTerms4.isChecked,
                    binding.chkTerms5.isChecked
                )
            }
            R.id.cancelBtn -> {
                finish()
            }

        }

    }

    private fun enableAcceptBtn(
        isFName: Boolean?,
        isLName: Boolean?,
        checkTerm: Boolean?,
        checkTerm1: Boolean?,
        checkTerm2: Boolean?,
        checkTerm3: Boolean?,
        checkTerm4: Boolean?,
        checkTerm5: Boolean?
    ): Boolean {
        if (isFName == true && isLName == true && checkTerm == true && checkTerm1 == true && checkTerm2 == true && checkTerm3 == true && checkTerm4 == true && checkTerm5 == true) {
            binding.acceptBtn.setBackgroundResource(R.drawable.chip_background)
            return true
        } else
            binding.acceptBtn.setBackgroundResource(R.drawable.gray_background)

        return false
    }
}