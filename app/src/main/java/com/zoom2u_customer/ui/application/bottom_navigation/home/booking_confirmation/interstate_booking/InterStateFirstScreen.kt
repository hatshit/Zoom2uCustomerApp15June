package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.interstate_booking


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityInterStateFirstScreenBinding
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class InterStateFirstScreen : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivityInterStateFirstScreenBinding
    private var bookingDeliveryResponse: JSONObject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_inter_state_first_screen)


        if (intent.hasExtra("MainJsonForMakeABooking")) {
            bookingDeliveryResponse = JSONObject(intent.getStringExtra("MainJsonForMakeABooking"))
        }
        binding.acceptBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        binding.chkTerms.setOnClickListener(this)
        binding.chkTerms1.setOnClickListener(this)
        binding.chkTerms2.setOnClickListener(this)
        binding.isLaptopMobile.setOnClickListener(this)


        val text =  getString(R.string.the_appropriate_label_has_been_attached_label_for_laptops_and_mobile_phones_can_be_downloaded_here)
        val spannableString = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=resources.getColor(R.color.base_color)
                ds.isUnderlineText = false
            }

            override fun onClick(p0: View) {
                try {
                    val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.label_download)
                    val browserIntent: Intent = Intent(
                        Intent.ACTION_VIEW,
                        getImageUri(applicationContext,image)
                    )
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //Toast.makeText(this@InterStateFirstScreen,"hera",Toast.LENGTH_LONG).show()
            }
        }
        spannableString.setSpan(clickableSpan, text.length-7, text.length-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.laptopText.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.laptopText.movementMethod = LinkMovementMethod.getInstance()



        if (bookingDeliveryResponse!!.getJSONObject("_deliveryRequestModel")
                .getString("isLaptopOrMobile").toString()=="laptopOrMobileYes"
        ){
            binding.laptopTerm.visibility=View.VISIBLE
            binding.isLaptopMobile.isChecked=false
        }else{
            binding.laptopTerm.visibility=View.GONE
            binding.isLaptopMobile.isChecked=true
        }
    }
    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
    
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.acceptBtn -> {
                if (enableAcceptBtn(binding.chkTerms.isChecked,binding.chkTerms1.isChecked,binding.chkTerms2.isChecked,binding.isLaptopMobile.isChecked)) {
                    val intent = Intent(this, InterStateSecondActivity::class.java)
                    intent.putExtra("MainJsonForMakeABooking", bookingDeliveryResponse.toString())
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    DialogActivity.alertDialogSingleButton(this, "Oops!", "Please fill out all mandatory fields marked in red.")
                }
            }
            R.id.chk_terms -> {
                enableAcceptBtn(binding.chkTerms.isChecked,binding.chkTerms1.isChecked,binding.chkTerms2.isChecked,binding.isLaptopMobile.isChecked)

            }
            R.id.chk_terms1 -> {
                enableAcceptBtn(binding.chkTerms.isChecked,binding.chkTerms1.isChecked,binding.chkTerms2.isChecked,binding.isLaptopMobile.isChecked)
            }
            R.id.chk_terms2 -> {
                enableAcceptBtn(binding.chkTerms.isChecked,binding.chkTerms1.isChecked,binding.chkTerms2.isChecked,binding.isLaptopMobile.isChecked)
            }
            R.id.is_laptop_mobile->{
                enableAcceptBtn(binding.chkTerms.isChecked,binding.chkTerms1.isChecked,binding.chkTerms2.isChecked,binding.isLaptopMobile.isChecked)
            }
            R.id.cancelBtn -> {
                finish()
            }

            }
    }

    private fun enableAcceptBtn(checkTerm: Boolean?, checkTerm1: Boolean?, checkTerm2: Boolean?,checkTerm3: Boolean?) :Boolean{
        if(checkTerm==true&&checkTerm1==true&&checkTerm2==true&&checkTerm3==true){
            binding.acceptBtn.setBackgroundResource(R.drawable.chip_background)
            return true
        }else
            binding.acceptBtn.setBackgroundResource(R.drawable.gray_background)

     return false
    }


}