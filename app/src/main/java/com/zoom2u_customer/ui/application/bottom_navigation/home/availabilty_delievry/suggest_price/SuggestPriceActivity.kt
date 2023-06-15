package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.suggest_price

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivitySuggestPriceBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req.quote_confirmation_page.QuoteConfirmationActivity
import com.zoom2u_customer.utility.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SuggestPriceActivity : AppCompatActivity(), View.OnClickListener {
    private var pricingRequestBody: JSONObject? = null
    lateinit var binding: ActivitySuggestPriceBinding
    private var datePicker: DatePicker? = null
    private var timePicker: TimePicker? = null
    private var timePicker2: TimePicker2? = null
    lateinit var viewModel: SuggestPriceViewModel
    private var repository: SuggestPriceRepository? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_suggest_price)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.view, this)
        viewModel = ViewModelProvider(this)[SuggestPriceViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = SuggestPriceRepository(serviceApi, this)
        viewModel.repository = repository



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
                    val browserIntent = Intent(
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


        if(intent.hasExtra("SaveDeliveryRequestReq")) {
            pricingRequestBody = JSONObject(intent.getStringExtra("SaveDeliveryRequestReq"))
        }

        setTimeInView()

        datePicker = DatePicker()
        timePicker = TimePicker()
        timePicker2 = TimePicker2()

        binding.pickTime.setOnClickListener(this)
        binding.pickDate.setOnClickListener(this)
        binding.pickDateCl.setOnClickListener(this)
        binding.pickTimeCl.setOnClickListener(this)
        binding.dropDateCl.setOnClickListener(this)
        binding.dropTimeCl.setOnClickListener(this)
        binding.dropTime.setOnClickListener(this)
        binding.dropDate.setOnClickListener(this)
        binding.suggestPriceBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)

        binding.price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != "" && s.toString() !=".") {
                    AppUtility.removeErrorBackGround(binding.price)
                } else if (s.toString() == "" || s.toString() =="." ) {
                    AppUtility.validateTextField(binding.price)
                    AppUtility.validateEditTextField(binding.price, "Please enter price.")
                }
            }
        })

        viewModel.getIntraPriceSuccess().observe(this) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    AppUtility.progressBarDissMiss()
                    val intent = Intent(this, QuoteConfirmationActivity::class.java)
                    intent.putExtra("QuoteId",it)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }

        viewModel.getInterPriceSuccess().observe(this) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    AppUtility.progressBarDissMiss()
                    val intent = Intent(this, QuoteConfirmationActivity::class.java)
                    intent.putExtra("HeavyFreightQuoteRef",it)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
        }
    }


    private fun setTimeInView() {
        try {
            /**date and time format*/
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val date = Date()

            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val currentTime = timeFormat.format(date)
            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
            val time9Pm = "09:00 PM"
            val time9PM: Date = timeFormat.parse(time9Pm)


            /**drop time*/
            val c = Calendar.getInstance()
            c.add(Calendar.HOUR, 3)
            val d = c.time
            val dropTime = timeFormat.format(d)
            binding.dropTime.text = dropTime.uppercase()
            binding.pickDate.text = dateFormat.format(date)
            binding.pickTime.text = currentTime.uppercase()



            if (time9PM.before(currTimeInDateFormat)) {
                val c = Calendar.getInstance()
                c.add(Calendar.DATE, 1)
                val d = c.time
                val dropTime = dateFormat.format(d)
                binding.dropDate.text = dropTime
            } else
                binding.dropDate.text = dateFormat.format(date)



        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.suggest_price_btn -> {
                if (binding.price.text.toString() == "") {
                    AppUtility.validateEditTextField(binding.price, "Please enter price.")
                    AppUtility.validateTextField(binding.price)
                    Toast.makeText(this,"Please fill out all mandatory fields marked in red.",Toast.LENGTH_SHORT).show()
                }else {
                    binding.ll1.visibility = View.GONE
                    binding.pricingLl.visibility = View.VISIBLE
                   /** make all view disable after from fill*/
                    binding.price.isEnabled=false
                    binding.pickTime.isEnabled=false
                    binding.pickDate.isEnabled=false
                    binding.pickDateCl.isEnabled=false
                    binding.pickTimeCl.isEnabled=false
                    binding.dropDateCl.isEnabled=false
                    binding.dropTimeCl.isEnabled=false
                    binding.dropTime.isEnabled=false
                    binding.dropDate.isEnabled=false
                    binding.finalPrice.text="$"+binding.price.text.toString()
                }
            }
            R.id.next_btn -> {
                if (binding.chkTerms.isChecked) {
                    DialogActivity.logoutDialog(
                        this,
                        "Confirm!",
                        "Due to the nature of this delivery, it will be created as a quote request and sent out to drivers for bidding on, instead of being a fixed quote. Our drivers will start providing quotes for this delivery, which you can view and accept. Do you wish to continue?",
                        "Yes","No",
                        onCancelClick=::onNoClick,
                        onOkClick = ::onYesClick
                    )
                }else{
                    Toast.makeText(this,"Please Accept the customer Terms and Conditions.", Toast.LENGTH_LONG).show()
                }
            }
            R.id.cancel_btn -> {
                val intent = Intent()
                setResult(3, intent)
                finish()
            }
            R.id.pick_date -> {
                datePicker?.datePickerDialog(
                    this,
                    binding.pickDate.text.toString(),
                    onItemClick = ::onPickDateClick
                )
            }
            R.id.pick_date_cl -> {
                datePicker?.datePickerDialog(
                    this,
                    binding.pickDate.text.toString(),
                    onItemClick = ::onPickDateClick
                )
            }
            R.id.pick_time -> {
                timePicker2?.timePickerDialog(
                    this, false,
                    binding.pickTime.text.toString(),
                    onItemClick = ::onPickTimeClick
                )
            }
            R.id.pick_time_cl -> {
                timePicker2?.timePickerDialog(
                    this, false,
                    binding.pickTime.text.toString(),
                    onItemClick = ::onPickTimeClick
                )
            }
            R.id.drop_date -> {
                datePicker?.datePickerDialog(
                    this,
                    binding.dropDate.text.toString(),
                    onItemClick = ::onDropDateClick
                )
            }
            R.id.drop_date_cl -> {
                datePicker?.datePickerDialog(
                    this,
                    binding.dropDate.text.toString(),
                    onItemClick = ::onDropDateClick
                )
            }
            R.id.drop_time -> {
                timePicker2?.timePickerDialog(
                    this, true,
                    binding.dropTime.text.toString(),
                    onItemClick = ::onDropTimeClick
                )
            }
            R.id.drop_time_cl -> {
                timePicker2?.timePickerDialog(
                    this, true,
                    binding.dropTime.text.toString(),
                    onItemClick = ::onDropTimeClick
                )
            }
            R.id.back_btn -> {
            val intent = Intent()
            setResult(3, intent)
            finish()
        }
        }
    }

    private fun onPickTimeClick(time: String?) {
        if (!TextUtils.isEmpty(time)) {
            if (checkPickFutureTime(time)) {
                binding.pickTime.text = time
                add3HourInPickTime(time)
            }
        }
    }


    private fun getPickDateAndTimeInEta(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.pickDate.text.toString() + " " +
                    binding.pickTime.text.toString()).toString()
    }


    private fun getDropDateAndTimeInEta(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.dropDate.text.toString() + " " +
                    binding.dropTime.text.toString()
        ).toString()
    }



    private fun onNoClick(){}

    private fun onYesClick() {

        pricingRequestBody?.getJSONObject("_requestModel")?.put("Price", binding.price.text.toString())
        pricingRequestBody?.getJSONObject("_requestModel")?.put("Notes", binding.notes.text.toString())

        pricingRequestBody?.getJSONObject("_requestModel")?.put("PickUpDateTime", getPickDateAndTimeInEta())
        pricingRequestBody?.getJSONObject("_requestModel")?.put("DropDateTime", getDropDateAndTimeInEta())
        if (pricingRequestBody?.getJSONObject("_requestModel")?.get("IsInterstate") == true) {
            viewModel.getPriceRequestInterState(pricingRequestBody)
        } else {

            viewModel.getPriceRequestIntraState(pricingRequestBody)
        }
    }

    private fun onDropDateClick(s: String?) {
        if (!TextUtils.isEmpty(s))
            binding.dropDate.text = s.toString()
    }

    private fun onPickDateClick(s: String?) {
        if (!TextUtils.isEmpty(s))
            binding.pickDate.text = s.toString()
        binding.dropDate.text = s.toString()

    }

    private fun onDropTimeClick(time: String?) {
        if (!TextUtils.isEmpty(time)) {
            if (checkDropFutureTime(time)) {
                binding.dropTime.text = time
            }
        }
    }

    private fun add3HourInPickTime(time: String?) {
        val serverDateTimeValue = binding.pickDate.text.toString() + " " + time
        val millisToAdd : Long= 1_0800_000
        val pickDate= DateTimeUtil.getTimeFromDateFormat1(serverDateTimeValue)
        try {
            val format: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val d = format.parse(pickDate)
            d.time = d.time + millisToAdd
            val dateFormatter = SimpleDateFormat("hh:mm aaa").format(d)
            if(!dateFormatter.isNullOrBlank())
                binding.dropTime.text=dateFormatter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun checkDropFutureTime(selectedTime: String?): Boolean {
        val serverDateTimeValue = binding.dropDate.text.toString() + " " +
                selectedTime
        val converter = SimpleDateFormat("EEE dd MMM yyyy hh:mm a")
        val convertedDate: Date?
        try {
            convertedDate = converter.parse(serverDateTimeValue)
            if (System.currentTimeMillis() > convertedDate.time) {
                DialogActivity.alertDialogSingleButton(
                    this,
                    "Oops!",
                    "Please enter a drop off date/time in the future."
                )
                return false
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun checkPickFutureTime(selectedTime: String?): Boolean {
        val serverDateTimeValue = binding.pickDate.text.toString() + " " +
                selectedTime
        val converter = SimpleDateFormat("EEE dd MMM yyyy hh:mm a")
        val convertedDate: Date?
        try {
            convertedDate = converter.parse(serverDateTimeValue)
            if (System.currentTimeMillis() > convertedDate.time) {
                DialogActivity.alertDialogSingleButton(
                    this,
                    "Oops!",
                    "Please enter a pickup date/time in the future."
                )
                return false
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(3, intent)
        finish()
    }

}