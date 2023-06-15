package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityAvailabilityDeliveryBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.model.InterStateRes
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.model.IntraStateRes
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.model.QuoteOptionClass
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.suggest_price.SuggestPriceActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingConfirmationActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.InterStateReq
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.IntraStateReq
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req.UploadQuotesActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import com.zoom2u_customer.utility.*
import org.json.JSONObject
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AvailabilityDeliveryActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivityAvailabilityDeliveryBinding
    private var datePicker: DatePicker? = null
    private var timePicker2: TimePicker2? = null

    private var mainObjForMakeABooking: JSONObject? = null
    private var saveDeliveryRequestReqForQuotes: JSONObject? = null
    private var pricingRequestBody: JSONObject? = null
    private var suggestPriceRequestReq: JSONObject? = null
    private var priceSelected: Boolean? = false
    private var intraStateReq: IntraStateReq? = null
    private var interStateReq: InterStateReq? = null
    lateinit var viewModel: AvailabilityDeliveryViewModel
    private var repository: AvailabilityDeliveryRepository? = null
    var adapter: AvailabilityDeliveryAdapter? = null
    private lateinit var itemDataList: ArrayList<Icon>
    private var selectedQuoteOptionClass: QuoteOptionClass? = null
    private var isGenerateQuotesBtn:Boolean?=null
    private var countDownTimer:CountDownTimer?=null
    private var isQuotesRequest: Boolean = false
    private var largeItem: Icon?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_availability_delivery)

        viewModel = ViewModelProvider(this)[AvailabilityDeliveryViewModel::class.java]
        val serviceApi: ServiceApi = getServices()
        repository = AvailabilityDeliveryRepository(serviceApi, this)
        viewModel.repository = repository

        setTimeInView()

        setAdapterView()

        if (intent.hasExtra("SaveDeliveryRequestReq")) {
            binding.priceCl.visibility = View.VISIBLE
            mainObjForMakeABooking = JSONObject(intent.getStringExtra("SaveDeliveryRequestReq"))
            suggestPriceRequestReq= JSONObject(intent.getStringExtra("SuggestPriceRequestReq"))
            callApiForInterOrIntraState()
            isQuotesRequest = intent.getBooleanExtra("isQuotesRequest", false)
        }else if(intent.hasExtra("SaveDeliveryRequestReqForQuotes")){
            binding.priceCl.visibility = View.GONE
            saveDeliveryRequestReqForQuotes=JSONObject(intent.getStringExtra("SaveDeliveryRequestReqForQuotes"))
            isQuotesRequest = intent.getBooleanExtra("isQuotesRequest", false)
        }

        /**for request quotes*/
        if (isQuotesRequest) {
            binding.nextBtn.text = "Request Quotes"
            binding.parcelBeforeCl.visibility = View.VISIBLE
        } else {
            binding.nextBtn.text = "Next"
            binding.parcelBeforeCl.visibility = View.GONE
        }




        datePicker = DatePicker()
        timePicker2 = TimePicker2()

        binding.pickTime.setOnClickListener(this)
        binding.pickDate.setOnClickListener(this)
        binding.pickDateCl.setOnClickListener(this)
        binding.pickTimeCl.setOnClickListener(this)
        binding.dropDateCl.setOnClickListener(this)
        binding.dropTimeCl.setOnClickListener(this)
        binding.dropTime.setOnClickListener(this)
        binding.dropDate.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.suggestPrice.setOnClickListener(this)


        /** get price using intra state*/
        viewModel.intraStateSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                if(countDownTimer !=null)
                    countDownTimer?.cancel()
                startTimer()
                val intraStateRes: IntraStateRes = Gson().fromJson(it, IntraStateRes::class.java)
                binding.priceView.visibility = View.VISIBLE
                binding.suggestPriceCl.visibility = View.VISIBLE
                binding.timeText.visibility = View.VISIBLE

                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("Vehicle", intraStateRes.Vehicle)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("Distance", intraStateRes.Distance)
                val quoteOptionList = intraStateRes.QuoteOptions
                if (quoteOptionList != null) {
                    adapter?.updateRecords(quoteOptionList)
                }
            }
        }

        /** get price using inter state*/
        viewModel.interStateSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                if(countDownTimer !=null)
                    countDownTimer?.cancel()
                startTimer()
                binding.priceView.visibility = View.VISIBLE
                binding.suggestPriceCl.visibility = View.VISIBLE
                binding.timeText.visibility = View.VISIBLE
                val interStateRes: InterStateRes = Gson().fromJson(it, InterStateRes::class.java)

                mainObjForMakeABooking!!.getJSONObject("_deliveryRequestModel").put("Vehicle", interStateRes.Vehicle)
                val quoteOptionList = interStateRes.QuoteOptions
                if (quoteOptionList != null) {
                    adapter?.updateRecords(quoteOptionList)
                }
            }
        }
        val text =  getString(R.string.expired_quote)
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
                regenerateQuotes()
            }
        }
        spannableString.setSpan(clickableSpan, 39, 43, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.quotesExpiered.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.quotesExpiered.movementMethod = LinkMovementMethod.getInstance()


        val text1 =  getString(R.string.suggest_price)
        val spannableString1 = SpannableString(text1)
        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=resources.getColor(R.color.base_color)
                ds.underlineColor=resources.getColor(R.color.base_color)
                ds.isUnderlineText = true
            }

            override fun onClick(p0: View) {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.zoom2u.com.au/suggest-a-price/")

                )
                startActivity(browserIntent)
            }
        }
        spannableString1.setSpan(clickableSpan1, 6, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.click.setText(spannableString1, TextView.BufferType.SPANNABLE)
        binding.click.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setTimeInView() {
        try {
            /**date and time format*/
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val date = Date()

            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val currentTime = timeFormat.format(date)
            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
            val time6Pm = "06:00 PM"
            val time6PM: Date = timeFormat.parse(time6Pm)
            if (time6PM.before(currTimeInDateFormat)) {
                val c = Calendar.getInstance()
                c.add(Calendar.DATE, 1)
                val d = c.time
                val dropTime = dateFormat.format(d)
                binding.pickDate.text = dropTime
                binding.pickTime.text = "08:00 AM"
                binding.dropDate.text = dropTime
                binding.dropTime.text = "11:00 AM"

            } else {
                binding.pickDate.text = dateFormat.format(date)
                binding.pickTime.text = currentTime.uppercase()

                /**drop time*/
                val c = Calendar.getInstance()
                c.add(Calendar.HOUR, 3)
                val d = c.time
                val dropTime = timeFormat.format(d)
                binding.dropTime.text = dropTime.uppercase()
                binding.dropDate.text = dateFormat.format(date)
            }

        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getPickDateAndTimeInEta(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
                binding.pickDate.text.toString() + " " +
                        binding.pickTime.text.toString()).toString()
        }


    private fun callApiForInterOrIntraState() {
        if (intent.hasExtra("IntraStateData")) {
            intraStateReq = intent.getParcelableExtra("IntraStateData")
            intraStateReq?.CurrentDateTime =AppUtility.getCurrentDateAndTimeInEta()
            itemDataList = intent.getParcelableArrayListExtra<Icon>("IconList") as ArrayList<Icon>
            intraStateReq?.PickupDateTime = getPickDateAndTimeInEta()
            viewModel.getIntraStatePrice(intraStateReq)
        } else if (intent.hasExtra("InterStateData")) {
            interStateReq = intent.getParcelableExtra("InterStateData")
            interStateReq?.CurrentDateTime = AppUtility.getCurrentDateAndTimeInEta()
            itemDataList = intent.getParcelableArrayListExtra<Icon>("IconList") as ArrayList<Icon>
            interStateReq?.PickupDateTime = getPickDateAndTimeInEta()
            viewModel.getInterStatePrice(interStateReq)
        }
    }

    fun setAdapterView() {
        val layoutManager = GridLayoutManager(this, 1)
        binding.priceView.layoutManager = layoutManager
        adapter = AvailabilityDeliveryAdapter(this, Collections.emptyList(), onItemClick = ::onPriceSelect)
        binding.priceView.adapter = adapter

    }

    private fun onPriceSelect(quoteOptionClass: QuoteOptionClass) {
        if (quoteOptionClass.isPriceSelect == false) {
            selectedQuoteOptionClass = quoteOptionClass
            priceSelected = true
        } else {
            priceSelected = false
        }
    }

    private fun startTimer() {
        countDownTimer  = object : CountDownTimer(180000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                binding.timer.text = f.format(min) + ":" + f.format(sec)
                binding.quotesExpiered.visibility = View.GONE
                if (millisUntilFinished < 60000) {
                    binding.timer.setTextColor(Color.RED)
                    binding.secMin.text="seconds."
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.secMin.text="minutes."
                binding.timer.setTextColor(Color.BLACK)
                binding.timer.text = "3:00"
                isGenerateQuotesBtn=true
                priceSelected = false
                binding.nextBtn.text="Regenerate Quotes"
                binding.quotesExpiered.visibility = View.VISIBLE
                adapter?.updateRecords(Collections.emptyList())

                binding.timeText.visibility = View.GONE

            }
        }.start()
    }

    private fun regenerateQuotes() {
        //setTimeInView()
        isGenerateQuotesBtn=false
        callApiForInterOrIntraState()
        priceSelected = false
        binding.quotesExpiered.visibility = View.GONE
        binding.nextBtn.text="Next"
        selectedQuoteOptionClass?.isPriceSelect = true
        adapter?.notifyDataSetChanged()
    }

    private fun selectedQuoteOptionData(quoteOptionClass: QuoteOptionClass?) {
        try {
            if (mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.getBoolean("IsInterstate") == true) {

                /**for InterstateModel*/
                mainObjForMakeABooking?.getJSONObject("_interstateModel")?.put("pickupDistance", quoteOptionClass?.PickupDistance)
                mainObjForMakeABooking?.getJSONObject("_interstateModel")?.put("dropDistance", quoteOptionClass?.DropDistance)
                mainObjForMakeABooking?.getJSONObject("_interstateModel")?.put("routeDropPrice", quoteOptionClass?.DropPrice)
                mainObjForMakeABooking?.getJSONObject("_interstateModel")?.put("routePickupPrice", quoteOptionClass?.PickupPrice)
                mainObjForMakeABooking?.getJSONObject("_interstateModel")?.put("routeAirPrice", quoteOptionClass?.InterstatePrice)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("DeliverySpeed", quoteOptionClass?.DeliverySpeed)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("BookingFee", quoteOptionClass?.BookingFee)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("Price", quoteOptionClass?.Price)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("Distance", quoteOptionClass?.Distance)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("DropDateTime", DateTimeUtil.getServerFormatFromServerFormat(quoteOptionClass?.DropDateTime))
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("ETA", quoteOptionClass?.ETA)
            } else
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("DeliverySpeed", quoteOptionClass?.DeliverySpeed)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("BookingFee", quoteOptionClass?.BookingFee)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("Price", quoteOptionClass?.Price)
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("DropDateTime", DateTimeUtil.getServerFormatFromServerFormat(quoteOptionClass?.DropDateTime))
                mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("ETA", quoteOptionClass?.ETA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.back_btn -> {
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
            R.id.next_btn -> {
               if (isQuotesRequest) {
                    val intent = Intent(this, UploadQuotesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("PickUpDateTime", getPickDateAndTimeInEta())
                    saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("DropDateTime", getDropDateAndTimeInEta())
                    intent.putExtra("SaveDeliveryRequestReqForQuotes", saveDeliveryRequestReqForQuotes.toString())
                    startActivityForResult(intent, 3)
                }else{
                    binding.nextBtn.isClickable=false
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.nextBtn.isClickable=true

                    }, 1000)

                    if(isGenerateQuotesBtn==true){
                        regenerateQuotes()
                    }
                    else {
                        if (priceSelected == true) {
                            selectedQuoteOptionData(selectedQuoteOptionClass)
                            val bookingConfirmationIntent =
                                Intent(this, BookingConfirmationActivity::class.java)
                            mainObjForMakeABooking?.getJSONObject("_deliveryRequestModel")?.put("PickUpDateTime", getPickDateAndTimeInEta())
                            bookingConfirmationIntent.putExtra("MainJsonForMakeABooking", mainObjForMakeABooking.toString())
                            bookingConfirmationIntent.putParcelableArrayListExtra("IconList", itemDataList)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivityForResult(bookingConfirmationIntent,3)
                        } else {
                            binding.nextBtn.isClickable = true
                           Toast.makeText(this, "Please select a price.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            R.id.suggest_price->{
                val intent = Intent(this, SuggestPriceActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtra("SaveDeliveryRequestReq",  suggestPriceRequestReq.toString())
                startActivityForResult(intent,3)

            }
        }
    }

    private fun getDropDateAndTimeInEta(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.dropDate.text.toString() + " " +
                    binding.dropTime.text.toString()
        ).toString()
    }

    private fun onPickDateClick(s: String?) {
        if (!s.isNullOrBlank()){
            binding.pickDate.text = s.toString()
            binding.dropDate.text = s.toString()
        }
       if(!isQuotesRequest)
        regenerateQuotes()
    }

    private fun onDropTimeClick(time: String?) {
        if (!time.isNullOrBlank()){
            if (checkDropFutureTime(time)) {
                binding.dropTime.text = time

            }
        }
    }

    private fun onDropDateClick(s: String?) {
        if (!s.isNullOrBlank())
            binding.dropDate.text = s.toString()
    }

    private fun onPickTimeClick(time: String?) {
        if (!time.isNullOrBlank()) {
            if (checkPickFutureTime(time)) {
                binding.pickTime.text = time
                add3HourInPickTime(time)
              //  checkIfPickTimeAfter9pm(time)
                if(isQuotesRequest==false)
                regenerateQuotes()

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**update current time when user back from price screen*/
        if (requestCode == 3) {
            selectedQuoteOptionClass?.isPriceSelect = true
            adapter?.notifyDataSetChanged()
            priceSelected = false
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val date = Date()

            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val currentTime = timeFormat.format(date)

            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)


            /**check if current time after 9pm */
            val time6Pm = "06:00 PM"
            val time6PM: Date = timeFormat.parse(time6Pm)

            if (time6PM.before(currTimeInDateFormat)) {
                val c = Calendar.getInstance()
                c.add(Calendar.DATE, 1)
                val d = c.time
                val dropTime = dateFormat.format(d)
                binding.pickDate.text = dropTime
                binding.pickTime.text = "08:00 AM"
                binding.dropDate.text = dropTime
                binding.dropTime.text = "11:00 AM"

            } else {
                binding.pickDate.text = dateFormat.format(date)
                binding.pickTime.text = currentTime.uppercase()

                /**drop time*/
                val c = Calendar.getInstance()
                c.add(Calendar.HOUR, 3)
                val d = c.time
                val dropTime = timeFormat.format(d)
                binding.dropTime.text = dropTime.uppercase()
                binding.dropDate.text = dateFormat.format(date)
            }

        }
    }
}