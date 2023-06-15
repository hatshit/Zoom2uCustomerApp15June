package com.zoom2u_customer.ui.application.bottom_navigation.profile.change_price

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityChangePriceBinding


class PriceChangeActivity : AppCompatActivity() {
    lateinit var binding: ActivityChangePriceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_price)

        setAdpterView()
    }

    fun setAdpterView() {
        var layoutManager = GridLayoutManager(this, 1)
        binding.priceChangeView.layoutManager = layoutManager
        var adapter = PriceChangeAdapter(this, PriceChangeProvider.priceChangeList.toList())
        binding.priceChangeView.adapter = adapter

    }
}