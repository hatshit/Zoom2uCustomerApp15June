package com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.availability_delivery_heavy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityAvailabilityDeliveryHeavyFreightBinding
import com.zoom2u_customer.databinding.ActivityAvailabilityDeliveryHeavyFreightDropDateBinding
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

class AvailabilityDeliveryHeavyFreightDropDateActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivityAvailabilityDeliveryHeavyFreightDropDateBinding
    private var datePicker: DatePicker? = null
    private var timePicker2: TimePicker2? = null
    private var largeItem: Icon?=null
    private var saveDeliveryRequestReqForQuotes: JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_availability_delivery_heavy_freight_drop_date)


        if(intent.hasExtra("LargeItem")){
            largeItem = intent.getParcelableExtra<Icon>("LargeItem")
            saveDeliveryRequestReqForQuotes=JSONObject(intent.getStringExtra("SaveDeliveryRequestReqForQuotes"))
        }
        datePicker = DatePicker()
        timePicker2 = TimePicker2()
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.pickTimeClDrop.setOnClickListener(this)
        binding.pickDateDrop.setOnClickListener(this)
        binding.pickDateClDrop.setOnClickListener(this)
        binding.pickTimeClDrop.setOnClickListener(this)
        binding.dropDateClDrop.setOnClickListener(this)
        binding.dropTimeClDrop.setOnClickListener(this)
        binding.dropTimeDrop.setOnClickListener(this)
        binding.dropDateDrop.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        setTimeInView()
    }


    private fun setTimeInView() {
        try {
           if(intent.getBooleanExtra("IsPickTimeSelected",false)){

               binding.pickDateDrop.text =intent.getStringExtra("PickupDate")
               binding.dropDateDrop.text =intent.getStringExtra("PickupDate")
               binding.pickTimeDrop.text = "02:00 PM"
               binding.dropTimeDrop.text = "06:00 PM"


           }else {


               /**date and time format*/
               val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
               val date = Date()

               val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
               val currentTime = timeFormat.format(date)
               val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
               val time7Pm = "07:00 PM"
               val time7PM: Date = timeFormat.parse(time7Pm)

               val time3Pm = "03:00 PM"
               val time3PM: Date = timeFormat.parse(time3Pm)

               val c1 = Calendar.getInstance()
               c1.add(Calendar.HOUR, 5)
               val d1 = c1.time
               val pickTime = timeFormat.format(d1)
               binding.pickTimeDrop.text = pickTime.uppercase()


               val c = Calendar.getInstance()
               c.add(Calendar.HOUR, 9)
               val d = c.time
               val dropTime = timeFormat.format(d)
               binding.dropTimeDrop.text = dropTime.uppercase()


               if (time7PM.before(currTimeInDateFormat)) {
                   val c = Calendar.getInstance()
                   c.add(Calendar.DATE, 1)
                   val d = c.time
                   val dropTime = dateFormat.format(d)
                   binding.dropDateDrop.text = dropTime.uppercase()
               } else if (time3PM.before(currTimeInDateFormat)) {
                   val c = Calendar.getInstance()
                   c.add(Calendar.DATE, 1)
                   val d = c.time
                   val dropTime = dateFormat.format(d)
                   binding.dropDateDrop.text = dropTime.uppercase()
                   binding.pickDateDrop.text = dateFormat.format(date)
               } else {
                   binding.dropDateDrop.text = dateFormat.format(date)
                   binding.pickDateDrop.text = dateFormat.format(date)
               }

           }
        }catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun getDropDateAndTimeInStart(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.pickDateDrop.text.toString() + " " +
                    binding.pickTimeDrop.text.toString()
        ).toString()
    }

    private fun getDropDateAndTimeEnd(): String {
        return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
            binding.dropDateDrop.text.toString() + " " +
                    binding.dropTimeDrop.text.toString()
        ).toString()
    }
    private fun onPickDateClick(s: String?) {
        if (!TextUtils.isEmpty(s)) {
            binding.pickDateDrop.text = s.toString()
            binding.dropDateDrop.text = s.toString()
        }
    }

    private fun onPickTimeClick(time: String?) {
        if (!TextUtils.isEmpty(time)) {
            if (checkPickFutureTime(time)) {
                binding.pickTimeDrop.text = time
                add3HourInPickTime(time)
            }
        }
    }

    private fun onDropTimeClick(time: String?) {
        if (!TextUtils.isEmpty(time)) {
            if (checkDropFutureTime(time)) {
                binding.dropTimeDrop.text = time
            }
        }
    }

    private fun checkDropFutureTime(selectedTime: String?): Boolean {
        val serverDateTimeValue = binding.dropDateDrop.text.toString() + " " +
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
            binding.dropDateDrop.text = s.toString()
    }

    private fun add3HourInPickTime(time: String?) {
        val serverDateTimeValue = binding.pickDateDrop.text.toString() + " " + time
        val millisToAdd : Long= 1_0800_000
        val pickDate= DateTimeUtil.getTimeFromDateFormat1(serverDateTimeValue)
        try {
            val format: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            val d = format.parse(pickDate)
            d.time = d.time + millisToAdd
            val dateFormatter = SimpleDateFormat("hh:mm aaa").format(d)
            if(!dateFormatter.isNullOrBlank())
                binding.dropTimeDrop.text=dateFormatter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun checkPickFutureTime(selectedTime: String?): Boolean {
        val serverDateTimeValue = binding.pickDateDrop.text.toString() + " " +
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
                val intent = Intent(this, HeavyFreightBidActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("DropDateTime",  getDropDateAndTimeInStart())
                saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("RequestedDropDateTimeWindowStart",  getDropDateAndTimeInStart())
                saveDeliveryRequestReqForQuotes?.getJSONObject("_requestModel")?.put("RequestedDropDateTimeWindowEnd", getDropDateAndTimeEnd())
                intent.putExtra("LargeItem", largeItem)
                intent.putExtra("SaveDeliveryRequestReqForQuotes", saveDeliveryRequestReqForQuotes.toString())
                startActivityForResult(intent, 3)
            }
              R.id.pick_date_drop -> {
                  datePicker?.datePickerDialog(
                      this,
                      binding.pickDateDrop.text.toString(),
                      onItemClick = ::onPickDateClick
                  )
              }
              R.id.pick_date_cl_drop -> {
                  datePicker?.datePickerDialog(
                      this,
                      binding.pickDateDrop.text.toString(),
                      onItemClick = ::onPickDateClick
                  )
              }
              R.id.pick_time_drop -> {
                  timePicker2?.timePickerDialog(
                      this, false,
                      binding.pickTimeDrop.text.toString(),
                      onItemClick = ::onPickTimeClick
                  )
              }
              R.id.pick_time_cl_drop -> {
                  timePicker2?.timePickerDialog(
                      this, false,
                      binding.pickTimeDrop.text.toString(),
                      onItemClick = ::onPickTimeClick
                  )
              }
              R.id.drop_date_drop -> {
                  datePicker?.datePickerDialog(
                      this,
                      binding.dropDateDrop.text.toString(),
                      onItemClick = ::onDropDateClick
                  )
              }
              R.id.drop_date_cl_drop -> {
                  datePicker?.datePickerDialog(
                      this,
                      binding.dropDateDrop.text.toString(),
                      onItemClick = ::onDropDateClick
                  )
              }
              R.id.drop_time_drop -> {
                  timePicker2?.timePickerDialog(
                      this, true,
                      binding.dropTimeDrop.text.toString(),
                      onItemClick = ::onDropTimeClick
                  )
              }
              R.id.drop_time_cl_drop -> {
                  timePicker2?.timePickerDialog(
                      this, true,
                      binding.dropTimeDrop.text.toString(),
                      onItemClick = ::onDropTimeClick
                  )
              }
        }
    }
}