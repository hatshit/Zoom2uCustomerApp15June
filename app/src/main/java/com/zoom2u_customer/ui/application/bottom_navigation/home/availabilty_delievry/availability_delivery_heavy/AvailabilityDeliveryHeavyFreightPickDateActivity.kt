package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.availability_delivery_heavy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityAvailabilityDeliveryHeavyFreightBinding
import com.zoom2u_customer.databinding.ActivityAvailabilityDeliveryHeavyFreightPickDateBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid.HeavyFreightBidActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import com.zoom2u_customer.utility.DatePicker
import com.zoom2u_customer.utility.DateTimeUtil
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.TimePicker2
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AvailabilityDeliveryHeavyFreightPickDateActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityAvailabilityDeliveryHeavyFreightPickDateBinding
    private var datePicker: DatePicker? = null
    private var timePicker2: TimePicker2? = null
    private var largeItem: Icon? = null
    private var saveDeliveryRequestReqForQuotes: JSONObject? = null
    private var isQuotesRequest: Boolean = false
    private var isPickTimeSelected=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_availability_delivery_heavy_freight_pick_date
        )


        if (intent.hasExtra("LargeItem")) {
            largeItem = intent.getParcelableExtra<Icon>("LargeItem")
            isQuotesRequest = intent.getBooleanExtra("isQuotesRequest", false)
            saveDeliveryRequestReqForQuotes = JSONObject(intent.getStringExtra("SaveDeliveryRequestReqForQuotes"))
        }

        datePicker = DatePicker()
        timePicker2 = TimePicker2()
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.pickTime.setOnClickListener(this)
        binding.pickDate.setOnClickListener(this)
        binding.pickDateCl.setOnClickListener(this)
        binding.pickTimeCl.setOnClickListener(this)
        binding.dropDateCl.setOnClickListener(this)
        binding.dropTimeCl.setOnClickListener(this)
        binding.dropTime.setOnClickListener(this)
        binding.dropDate.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        setTimeInView()
    }


    private fun setTimeInView() {
        try {
            /**date and time format*/
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val date = Date()

            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val currentTime = timeFormat.format(date)
            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
            val time8Pm = "08:00 PM"
            val time8PM: Date = timeFormat.parse(time8Pm)


            /**drop time*/
            val c = Calendar.getInstance()
            c.add(Calendar.HOUR, 4)
            val d = c.time
            val dropTime = timeFormat.format(d)
            binding.dropTime.text = dropTime.uppercase()

            binding.pickDate.text = dateFormat.format(date)
            binding.pickTime.text = currentTime.uppercase()



            if (time8PM.before(currTimeInDateFormat)) {
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


    private fun getPickDateAndTimeStart(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.pickDate.text.toString() + " " +
                    binding.pickTime.text.toString()
        ).toString()
    }

    private fun getPickDateAndTimeInEnd(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.dropDate.text.toString() + " " +
                    binding.dropTime.text.toString()
        ).toString()
    }
    private fun onPickDateClick(s: String?) {
        val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
        val date = Date()
        if (!TextUtils.isEmpty(s)) {
            binding.pickDate.text = s.toString()
            binding.dropDate.text = s.toString()
            if(s==dateFormat.format(date)){
                setTimeInView()
                isPickTimeSelected=false
            }else{
                isPickTimeSelected=true
                binding.pickTime.text = "09:00 AM"
                binding.dropTime.text = "01:00 PM"
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

    private fun onDropTimeClick(time: String?) {
        if (!TextUtils.isEmpty(time)) {
            if (checkDropFutureTime(time)) {
                binding.dropTime.text = time

            }
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

    private fun onDropDateClick(s: String?) {
        if (!TextUtils.isEmpty(s))
            binding.dropDate.text = s.toString()
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

        override fun onClick(view: View?) {
        when (view?.id) {
            R.id.back_btn -> {
                finish()
            }
            R.id.next_btn -> {
                val intent = Intent(this, AvailabilityDeliveryHeavyFreightDropDateActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("PickUpDateTime", getPickDateAndTimeStart())
                saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("RequestedPickupDateTimeWindowStart", getPickDateAndTimeStart())
                saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("RequestedPickupDateTimeWindowEnd", getPickDateAndTimeInEnd())
                intent.putExtra("IsPickTimeSelected",isPickTimeSelected)
                intent.putExtra("PickupDate",binding.pickDate.text)
                intent.putExtra("isQuotesRequest",isQuotesRequest)
                intent.putExtra("LargeItem", largeItem)
                intent.putExtra("SaveDeliveryRequestReqForQuotes", saveDeliveryRequestReqForQuotes.toString())
                startActivityForResult(intent, 3)
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
        }
    }
}