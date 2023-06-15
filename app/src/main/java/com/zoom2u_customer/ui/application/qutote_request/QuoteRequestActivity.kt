package com.zoom2u_customer.ui.application.qutote_request

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityQuoterequestBinding

class QuoteRequestActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivityQuoterequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_quoterequest)
        binding.closePage.setOnClickListener(this)
        binding.chatBtn.setOnClickListener(this)
        binding.backBtn.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.close_page -> {
                overridePendingTransition(0, R.anim.slide_down)
            }
        }
    }
}