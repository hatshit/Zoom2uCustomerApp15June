package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req.quote_confirmation_page

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityQuoteConfirmationBinding
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.ActiveBidActivity
import com.zoom2u_customer.utility.CustomTypefaceSpan

class QuoteConfirmationActivity : AppCompatActivity() {
    private var activeBidItem: ActiveBidListResponse? = null
    lateinit var binding: ActivityQuoteConfirmationBinding
    private var quoteID: String? = null
    private var heavyFreightQuoteRef: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quote_confirmation)


        if (intent.hasExtra("QuoteId")) {
            quoteID = intent.getStringExtra("QuoteId").toString()
        }else if(intent.hasExtra("HeavyFreightQuoteRef")) {
            heavyFreightQuoteRef = intent.getStringExtra("HeavyFreightQuoteRef")
            activeBidItem = intent.getParcelableExtra<ActiveBidListResponse>("ActiveBidItemFromHeavyFreight")
        }
        val importantTxtStr =
            "Status: Awaiting quotes from couriers. These should arrive within 10mins."
        val ss = SpannableStringBuilder(importantTxtStr)
        ss.setSpan(CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.arimo_bold)!!), 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        ss.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.arimo_regular)!!), 10,
            importantTxtStr.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.txtAwaitingQuotes.text=ss

        binding.close.setOnClickListener(){
            newBooking()
        }

        binding.getQuoteBtn.setOnClickListener(){
            val intent1 = Intent(this, ActiveBidActivity::class.java)
            if(intent.hasExtra("QuoteId"))
                intent1.putExtra("QuoteId1",quoteID)
            else if(intent.hasExtra("HeavyFreightQuoteRef")) {
                intent1.putExtra("HeavyFreightQuoteRef", heavyFreightQuoteRef)
                intent1.putExtra("ActiveBidItemFromHeavyFreight",activeBidItem)
            }
               intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent1)

        }

    }

    private fun newBooking(){
        val intent1 = Intent("bid_refresh1")
        LocalBroadcastManager.getInstance(this@QuoteConfirmationActivity).sendBroadcast(intent1)
        val intent = Intent(this, BasePageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        finish()

    }

    override fun onBackPressed() {
       newBooking()
    }

}