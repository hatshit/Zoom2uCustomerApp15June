package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.braintreepayments.api.DropInClient
import com.braintreepayments.api.DropInListener
import com.braintreepayments.api.DropInRequest
import com.braintreepayments.api.DropInResult

import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityOnHoldBinding

import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.application.bottom_navigation.history.HistoryResponse
import com.zoom2u_customer.ui.application.bottom_navigation.history.history_details.HistoryDetailsRepository
import com.zoom2u_customer.ui.application.bottom_navigation.history.history_details.HistoryDetailsResponse
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingResponse
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONException

class OnHoldActivity : AppCompatActivity() , DropInListener {
    lateinit var binding: ActivityOnHoldBinding
    var historyResponse: HistoryResponse?=null
    var historyDetailsResponse: HistoryDetailsResponse? = null
    private var bookingResponse: BookingResponse? = null
    lateinit var viewModel: OnHoldViewModel
    private var dropInClient : DropInClient?=null
    private var repository: OnHoldRepository? = null
    var repositoryHistory: HistoryDetailsRepository? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_hold)
        dropInClient = DropInClient(this, AppPreference.getSharedPrefInstance().getBrainToken())

        if (intent.hasExtra("BookingResponse")) {
            bookingResponse = intent.getParcelableExtra("BookingResponse")
            setData(bookingResponse)
        }else if (intent.hasExtra("historyResponse")) {
            historyResponse = intent.getParcelableExtra("historyResponse")
            binding.txtBookingRefrence.text = historyResponse?.BookingRef
        }else if (intent.hasExtra("historyDetailsResponse")) {
            historyDetailsResponse = intent.getParcelableExtra("historyDetailsResponse")
            binding.txtBookingRefrence.text = historyDetailsResponse?.BookingRef
        }
        viewModel = ViewModelProvider(this).get(OnHoldViewModel::class.java)
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = OnHoldRepository(serviceApi, this)
        repositoryHistory = HistoryDetailsRepository(serviceApi, this)
        viewModel.repository = repository
        viewModel.repositoryHistory = repositoryHistory

        binding.acceptBtn.setOnClickListener {
           /* binding.acceptBtn.isClickable=false
            Handler(Looper.getMainLooper()).postDelayed({
                binding.acceptBtn.isClickable=true

            }, 3000)*/
            val dropInRequest = DropInRequest()
            dropInClient?.setListener(this)
            dropInClient?.launchDropIn(dropInRequest)
        }

        binding.cancelBtn.setOnClickListener(){
            DialogActivity.logoutDialog(
                this,
                "Confirm!",
                "Are you sure you want to cancel your booking?",
                "Yes", "No",
                onCancelClick = ::onNoClick,
                onOkClick = ::onYesClick)
        }

        binding.zoom2uHeader.backBtn.setOnClickListener{
          newBooking()
        }


        viewModel.getActivateSuccess()?.observe(this) {
            if (it != null) {
                if (it == "true") {
                    AppUtility.progressBarDissMiss()
                    if (intent.hasExtra("BookingResponse")){
                        val intent = Intent(this, OrderConfirmActivity::class.java)
                        intent.putExtra("BookingResponse", bookingResponse)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(intent)
                        finish()
                    }
                 else{
                        val intent = Intent(this, OrderConfirmActivity::class.java)
                        intent.putExtra("historyResponse", historyResponse)
                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivityForResult(intent,23)

                    }


                }
            }
        }
        viewModel.getCancelBooking().observe(this) {
            if (it != null) {
                if (it == "true") {
                    AppUtility.progressBarDissMiss()
                    if (intent.hasExtra("BookingResponse")) {
                        val intent = Intent(this, BasePageActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        Toast.makeText(this, "Booking cancellation successfully.", Toast.LENGTH_LONG).show()
                        startActivity(intent)
                        finish()
                    }
                    /**call when cancel from history on Hold*/
                    else{
                        val intent = Intent()
                        historyResponse?.IsCancel = true
                        intent.putExtra("historyItem", historyResponse)
                        setResult(85, intent)
                        Toast.makeText(this, "Booking cancellation successfully.", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }
            }
        }
    }

    private fun setData(bookingResponse: BookingResponse?) {
        binding.txtBookingRefrence.text = bookingResponse?.BookingRef

    }

    override fun onDropInSuccess(dropInResult: DropInResult) {
        val paymentMethodNonce = dropInResult.paymentMethodNonce?.string
        try {
            when {
                intent.hasExtra("BookingResponse") -> viewModel.activateRequest(
                    bookingResponse?.BookingRef,
                    paymentMethodNonce
                )
                intent.hasExtra("historyResponse") -> viewModel.activateRequest(
                    historyResponse?.BookingRef,
                    paymentMethodNonce
                )
                else -> viewModel.activateRequest(
                    historyDetailsResponse?.BookingRef,
                    paymentMethodNonce
                )
            }
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


    private fun onNoClick() {}

    private fun onYesClick() {
        if (intent.hasExtra("BookingResponse"))
          viewModel.cancelBooking(bookingResponse?.BookingId)
        else if (intent.hasExtra("historyResponse")){
            if(historyResponse?.IsCancel==true)
                DialogActivity.alertDialogSingleButton(this, "Error", "Unfortunately your booking could not be cancelled: Validation Errors")
            else
            viewModel.cancelBooking(historyResponse?.BookingId.toString())
        }else if (intent.hasExtra("historyDetailsResponse")){
            if(historyDetailsResponse?.IsCancel==true)
                DialogActivity.alertDialogSingleButton(this, "Error", "Unfortunately your booking could not be cancelled: Validation Errors")
            else
                viewModel.cancelBooking(historyDetailsResponse?.BookingId.toString())
        }


    }


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request_Code) {
            when(resultCode) {
                RESULT_OK -> {
                    val result: DropInResult? =
                        data?.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                    val nonce1: PaymentMethodNonce? = result?.paymentMethodNonce
                    val nonce = nonce1?.nonce
                    try {
                        when {
                            intent.hasExtra("BookingResponse") -> viewModel.activateRequest(
                                bookingResponse?.BookingRef,
                                nonce
                            )
                            intent.hasExtra("historyResponse") -> viewModel.activateRequest(
                                historyResponse?.BookingRef,
                                nonce
                            )
                            else -> viewModel.activateRequest(
                                historyDetailsResponse?.BookingRef,
                                nonce
                            )
                        }
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
            }
        }else if(requestCode==23){
            val updatedHistoryItem: HistoryResponse? = data?.getParcelableExtra<HistoryResponse>("historyItem")
            val intent = Intent()
            intent.putExtra("historyItem1",updatedHistoryItem)
            setResult(85,intent)
            finish()
        }
    }
*/
    override fun onBackPressed() {
         newBooking()

    }



    private fun newBooking() {
        val intent1 = Intent("home_page")
        intent1.putExtra("message","form_on_hold_page")
        LocalBroadcastManager.getInstance(this@OnHoldActivity).sendBroadcast(intent1)


        val intent = Intent(this, BasePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()
    }

}