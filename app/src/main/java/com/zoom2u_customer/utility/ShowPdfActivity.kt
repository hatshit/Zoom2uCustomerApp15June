package com.zoom2u_customer.utility

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityShowPdfBinding


class ShowPdfActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowPdfBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding = DataBindingUtil.setContentView(this, R.layout.activity_show_pdf)

        binding.pdfView.loadUrl("item_not_send.pdf")

    }
}