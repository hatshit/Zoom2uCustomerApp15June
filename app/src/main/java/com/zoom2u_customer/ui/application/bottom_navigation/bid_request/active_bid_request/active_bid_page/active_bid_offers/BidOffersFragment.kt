package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.braintreepayments.api.DropInClient
import com.braintreepayments.api.DropInListener
import com.braintreepayments.api.DropInRequest
import com.braintreepayments.api.DropInResult
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.FragmentBidOffersBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.ActiveBidActivity
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.BidDetailsResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy.UpdateDetailsHeavyActivity
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_biddetails.BidDetailsFragment
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingResponse
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OnHoldActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OrderConfirmActivity
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidChatList
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidLoadChatBookingArray
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidMessageActivity
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONException

class BidOffersFragment() : Fragment(), DropInListener {
    lateinit var binding: FragmentBidOffersBinding
    lateinit var viewModel: ActiveBidOffersViewModel
    private var repository: ActiveBidOffersRepository? = null
    var dropInClient : DropInClient?=null
    private var offers: Offer? = null
    private var purOrderNo: String? = null
    var bidDetails: BidDetailsResponse? = null
    private var heavyFreightBidDetails: HeavyFreightBidDetails? =null
    companion object {
        var adapter: ActiveBidOffersAdapter? = null
        var bidDetail1: BidDetailsResponse? = null
        var heavyFreightBidDetails1: HeavyFreightBidDetails? =null

        fun newInstance(bidDetails: BidDetailsResponse?,heavyFreightBidDetails: HeavyFreightBidDetails?): BidOffersFragment{
            this.bidDetail1 = bidDetails
            this.heavyFreightBidDetails1 = heavyFreightBidDetails
            return BidOffersFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBidOffersBinding.inflate(inflater, container, false)
        this.bidDetails=bidDetail1
        this.heavyFreightBidDetails = BidDetailsFragment.heavyFreightBidDetails1
        if (container != null) {
            if(bidDetails!=null)
            setAdapterView(binding, container.context)
            else if(heavyFreightBidDetails!=null)
              setAdapterView1(binding,container.context)
        }
        viewModel = ViewModelProvider(this)[ActiveBidOffersViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = ActiveBidOffersRepository(serviceApi, container?.context)
        viewModel.repository = repository
        dropInClient = DropInClient(this,AppPreference.getSharedPrefInstance().getBrainToken())

        viewModel.getQuotePayment()?.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                AppUtility.progressBarDissMiss()
                val bookingResponse: BookingResponse =
                    Gson().fromJson(it, BookingResponse::class.java)
                if (!bookingResponse.`$type`
                        .equals("System.Web.Http.HttpError, System.Web.Http", ignoreCase = true)
                ) {
                    if (bookingResponse.Verified == true) {
                        val intent = Intent(activity, OrderConfirmActivity::class.java)
                        intent.putExtra("BookingRefFromBid", bookingResponse.BookingRef)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        activity?.finish()
                    }else {
                        val intentOnHold = Intent(activity, OnHoldActivity::class.java)
                        intentOnHold.putExtra("BookingResponse", bookingResponse)
                        intentOnHold.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intentOnHold)
                        activity?.finish()
                    }
                }
            }
        }




        return binding.root
    }


    override fun onDropInSuccess(dropInResult: DropInResult) {
        val paymentMethodNonce = dropInResult.paymentMethodNonce?.string
        try {
            viewModel.quotePayment(
                paymentMethodNonce.toString(),
                bidDetails?.Id.toString(),
                offers?.OfferId.toString(),
                purOrderNo.toString()
            )

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onDropInFailure(error: java.lang.Exception) {
        Toast.makeText(
            activity,
            "Payment initiation failed, please try again.",
            Toast.LENGTH_SHORT
        ).show();
    }

    fun setAdapterView(binding: FragmentBidOffersBinding, context: Context) {
        if(bidDetails?.Offers?.isNotEmpty() == true) {
            val layoutManager = GridLayoutManager(activity, 1)
            binding.activeBidOffersRecycler.layoutManager = layoutManager

             adapter = ActiveBidOffersAdapter(
                context,
                bidDetails?.Offers?.toMutableList()!!,
                onItemClick = ::onBidOfferSelected,
                onChatClick = ::onBidForChat,
                 onRejectBidClick = ::onRejectOffer,
                 false)
            binding.activeBidOffersRecycler.adapter = adapter
            if (ActiveBidLoadChatBookingArray.arrayOfChatDelivery.size != 0){
                adapter?.updateRecords(ActiveBidLoadChatBookingArray.arrayOfChatDelivery)
            }
        }else{
            binding.emptyView.visibility=View.VISIBLE
        }
    }

    private fun setAdapterView1(binding: FragmentBidOffersBinding, context: Context) {
        if(heavyFreightBidDetails?.Offers?.isNotEmpty() == true) {
            val layoutManager = GridLayoutManager(activity, 1)
            binding.activeBidOffersRecycler.layoutManager = layoutManager
            adapter = ActiveBidOffersAdapter(
                context,
                heavyFreightBidDetails?.Offers?.toMutableList()!!,
                onItemClick = ::onHeavyBidOfferSelected,
                onChatClick = ::onBidForChat,
                onRejectBidClick = ::onRejectOffer,
                true)
            binding.activeBidOffersRecycler.adapter = adapter
            if (ActiveBidLoadChatBookingArray.arrayOfChatDelivery.size != 0){
                adapter?.updateRecords(ActiveBidLoadChatBookingArray.arrayOfChatDelivery)
            }
        }else{
            binding.emptyView.visibility=View.VISIBLE
        }
    }

    private fun onBidForChat(chatList: ActiveBidChatList) {
        try {
            val intent = Intent(activity, ActiveBidMessageActivity::class.java)
            intent.putExtra("BidChat", chatList)
            startActivity(intent)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun onRejectOffer(offer: Offer){
        val dialogView: View =
            LayoutInflater.from(activity).inflate(R.layout.confrm_dialogview, null, false)

        val builder = activity?.let { AlertDialog.Builder(it) }

        builder?.setView(dialogView)

        var alertDialog= builder?.create()

        alertDialog?.show()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val titleAlert: TextView = dialogView.findViewById(R.id.dialogLogoutTitleText)
        titleAlert.text = "Confirm"

        val msgAlertDialog: TextView = dialogView.findViewById(R.id.dialogLogoutMessageText)
        msgAlertDialog.text = "Are you sure you want to reject this bid?"


        val cancel: TextView = dialogView.findViewById(R.id.cancel)
        cancel.text="No"
        cancel.setOnClickListener {
            alertDialog?.dismiss()
        }

        val ok: TextView = dialogView.findViewById(R.id.ok)
        ok.text="Yes"
        ok.setOnClickListener {
            viewModel.rejectOffer(offer,::removeRejectedOffer)
            alertDialog?.dismiss()
        }
        val crossBtn: ImageView = dialogView.findViewById(R.id.dialogDoneBtn)
        crossBtn.setOnClickListener {
            alertDialog?.dismiss()
        }

  /*      var dialog: AlertDialog?=null
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Confirm")
        alertDialog.setMessage("Are you sure you want to reject this bid?")
        alertDialog.setPositiveButton("Yes") { _, _ ->
            viewModel.rejectOffer(offer,::removeRejectedOffer)
            dialog?.dismiss()
        }

        alertDialog.setNegativeButton("No") { _, _ ->
            dialog?.dismiss()
        }

        dialog = alertDialog.create()
        dialog?.show()*/
    }


    private fun removeRejectedOffer(offer: Offer){
        adapter?.removeOffer(offer)

        adapter?.itemCount?.let { count->
            if (count==0)
            binding.emptyView.visibility=View.VISIBLE

            (activity as ActiveBidActivity).updateOfferTabTitle(count)

        }
    }

    private fun onBidOfferSelected(offer: Offer) {
        this.offers = offer

        val viewGroup = (context as Activity).findViewById<ViewGroup>(R.id.content)
        val dialogView: View =
            LayoutInflater.from(context).inflate(R.layout.purchase_dialogview, viewGroup, false)
        val builder = AlertDialog.Builder(context as Activity)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val orderNo: TextInputEditText = dialogView.findViewById(R.id.pur_no)


        val submitBtn: TextView = dialogView.findViewById(R.id.ok)
        submitBtn.setOnClickListener {
            if (orderNo.text.toString().trim() == "")
                AppUtility.validateEditTextField(orderNo, "Please enter purchase order number.")
            else {
                purOrderNo = orderNo.text.toString().trim()
              /**check account type for payment*/
                if (AppPreference.getSharedPrefInstance().getAccountType() == "\"Standard\"") {
                    val dropInRequest = DropInRequest()
                    dropInClient?.setListener(this)
                    dropInClient?.launchDropIn(dropInRequest)
                }else {
                    AppUtility.progressBarShow(activity)
                    if(bidDetails!=null)
                     viewModel.quotePayment("", bidDetails?.Id.toString(), offers?.OfferId.toString(), purOrderNo.toString())
                }
                alertDialog.dismiss()
            }
        }
        val cancelBtn: TextView = dialogView.findViewById(R.id.cancel)
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }

    }

    private fun onHeavyBidOfferSelected(offer: Offer) {
        val intent = Intent(activity, UpdateDetailsHeavyActivity::class.java)
        intent.putExtra("HeavyFreightBidDetails", heavyFreightBidDetails)
        intent.putExtra("offer", offer)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == request_Code) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    val result: DropInResult? =
                        data?.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                    val nonce1: PaymentMethodNonce? = result?.paymentMethodNonce
                    val nonce = nonce1?.nonce
                    try {
                        viewModel.quotePayment(
                            nonce.toString(),
                            bidDetails?.Id.toString(),
                            offers?.OfferId.toString(),
                            purOrderNo.toString()
                        )

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    Toast.makeText(
                        activity,
                        "Payment initiation failed, please try again.",
                        Toast.LENGTH_SHORT
                    ).show();
                }
                else -> {
                    val error: Exception =
                        data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    Toast.makeText(activity, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
*/

    override fun onResume() {
        super.onResume()
        ActiveBidLoadChatBookingArray.setCourierToOnlineForChat();
        if (adapter != null)
            adapter?.notifyDataSetChanged()
    }
}