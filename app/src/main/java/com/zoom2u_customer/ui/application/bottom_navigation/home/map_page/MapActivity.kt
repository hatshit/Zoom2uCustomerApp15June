package com.zoom2u_customer.ui.application.bottom_navigation.home.map_page

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityMapBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.pickup_details.PickUpDetailsActivity
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.CustomTypefaceSpan
import com.zoom2u_customer.utility.DialogActivity
import java.text.DecimalFormat

class MapActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var binding: ActivityMapBinding
    private lateinit var adapter: ItemMapDocCountAdapter
    private lateinit var dataList: ArrayList<Icon>
    var isQuotesRequest:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl1, this)
        val intent: Intent = intent
        dataList = intent.getParcelableArrayListExtra<Icon>("icon_data") as ArrayList<Icon>

        setAdapterView()

        // binding.chatBtn.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)

        val getTotalWeightSum= getTotalWeight(dataList).toString()
        binding.weight.text= "$getTotalWeightSum Kg"
        checkQuotesWhenFirstTimeLunch(getTotalWeightSum)

        val text = "<font color=#ff0000>Please login with your account details on</font> " +
                "<font color=#00A7E2>https://deliveries.zoom2u.com/#/register-login  </font>" +
                "<font color=#ff0000>to create Heavy and Large freight bookings.</font>"
        binding.errorText.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)


        val importantTxtStr =
            "Important: Items weighing over 30kg each or 100kg in total will need to be placed through our Bid Request services. The same goes for items with measurements exceeding 200cm and multiple number of items exceeding the limit per booking. This is to maintain the safety and good health of our drivers."
        val ss = SpannableStringBuilder(importantTxtStr)
        ss.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.arimo_bold)!!),
            0,
            10,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            CustomTypefaceSpan("", ResourcesCompat.getFont(this, R.font.arimo_regular)!!), 10,
            importantTxtStr.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        binding.noteText1.text = ss





    }

    fun setAdapterView() {
        val layoutManager = GridLayoutManager(this, 1)
        binding.iconView.layoutManager = layoutManager
        adapter = ItemMapDocCountAdapter(this, dataList, onTotalWeight = ::onTotalWeight)
        binding.iconView.adapter = adapter
    }


    private fun onTotalWeight(totalWeight:String,icon: Icon){
        val df = DecimalFormat("#.###")
        binding.weight.text= df.format(totalWeight.toDouble()).toString() + "Kg"
        if(totalWeight.toDouble()>100 ||  !getLengthHeightWidthCheck() || !getCountCheck()) {
            binding.noteText.visibility = View.VISIBLE
            isQuotesRequest = true
        }else{
            binding.noteText.visibility=View.GONE
            isQuotesRequest = false
        }
    }


    private fun checkQuotesWhenFirstTimeLunch(totalWeight:String){
        if(totalWeight.toDouble()>100 || !getCountCheck()){
            binding.noteText.visibility = View.VISIBLE
            isQuotesRequest = true
        }else{
            binding.noteText.visibility=View.GONE
            isQuotesRequest = false
        }
    }


    private fun getLengthHeightWidthCheck() : Boolean{
        for (icon in dataList) {
            if(icon.length > 200 || icon.height > 200 || icon.width > 200 )
            return false
        }
        return true
    }


    private fun getCountCheck(): Boolean {
        for (item in dataList) {
            if(item.Value==10&&item.quantity>30){
                return false
            }else if(item.Value==11&&item.quantity>15){
                return false
            }else if(item.Value==12&&item.quantity>15){
                return false
            }else if(item.Value==13&&item.quantity>15){
                return false
            }else if(item.Value==14&&item.quantity>4){
                return false
            }
        }
        return true
    }

    private fun checkAllFieldAreFilled():Boolean{
        for (item in dataList) {
            if(item.quantity==0){
                Toast.makeText(this,"Selected item quantity can't be zero",Toast.LENGTH_LONG).show()
                return false
            }else if(item.length==0 || item.height==0 || item.width==0){
                Toast.makeText(this,"Shipment dimensions can’t be zero.",Toast.LENGTH_LONG).show()
                return false
            }
            else if(item.quantity==-1 || item.weight==-1.0 || item.length==-1 || item.height==-1 || item.width==-1){
                Toast.makeText(this,"Please fill out all mandatory fields marked in red.",Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }





    private fun getTotalWeight(dataList: ArrayList<Icon>): Double {
        var totalWeight = 0.0
        for (item in dataList) {
            totalWeight += item.weight*item.quantity
        }
        return totalWeight
    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.next_btn -> {
                binding.nextBtn.isClickable=false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.nextBtn.isClickable=true

                }, 1000)

               if(checkAllFieldAreFilled()) {
                   val intent = Intent(this, PickUpDetailsActivity::class.java)
                   intent.putParcelableArrayListExtra("IconList", dataList)
                   intent.putExtra("isQuotesRequest", isQuotesRequest)
                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                   startActivity(intent)
               }
            }
            R.id.back_btn -> {
                DialogActivity.logoutDialog(
                    this,
                    "Abandon this booking?",
                    "This booking's information will be lost if you cancel.",
                    "Continue", "Abandon",
                    onCancelClick = ::onCancelClick,
                    onOkClick = ::onOkClick
                )
            }
            /* R.id.chat_btn -> {
                 val intent = Intent(this, ChatActivity::class.java)
                 startActivity(intent)
             }*/

        }
    }

    private fun onCancelClick() {
        val intent = Intent()
        setResult(11, intent)
        finish()
    }

    private fun onOkClick() {}

    override fun onBackPressed() {
        DialogActivity.logoutDialog(
            this,
            "Abandon this booking?",
            "This booking's information will be lost if you cancel.",
            "Continue", "Abandon",
            onCancelClick = ::onCancelClick,
            onOkClick = ::onOkClick
        )
    }

}