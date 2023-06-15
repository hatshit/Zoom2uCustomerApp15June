package com.zoom2u_customer.utility

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.aigestudio.wheelpicker.WheelPicker
import com.zoom2u_customer.R

class TimePicker2 : WheelPicker.OnItemSelectedListener {
    private lateinit var mContext: Context
    private var isSelectTimeWindow = 0
    private var dialogTimePicker: Dialog? = null
    private lateinit var wheelHr: WheelPicker
    private lateinit var wheelMin: WheelPicker
    private lateinit var wheelAmPm: WheelPicker
    private val pickHr =
        arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    private val pickMin =
        arrayOf("00", "15", "30", "45")
    private val pickAmPm =
        arrayOf("AM", "PM")

    private var selectedHr: String ? = null
    private var selectedMin: String ? = null
    private var selectedAmPm:String ? = null
    private var selectedTime:String?=null
    private var isForDropTime: Boolean = false
    fun timePickerDialog(
        context: Context,
        isForDrop: Boolean,
        selectedTimeWindowItem: String,
        onItemClick: (hr: String?) -> Unit
    ) {
        this.isForDropTime = isForDrop
        mContext = context
        isSelectTimeWindow = 0

        dialogTimePicker?.let {
            if (it.isShowing) it.dismiss()
            dialogTimePicker = null
        }


        val arr= selectedTimeWindowItem.split(":").toTypedArray()
        selectedHr=arr[0]
        val arr1= arr[1].split(" ").toTypedArray()
        selectedMin="00"
        selectedAmPm= arr1[1]

        /*  val min= arr1?.get(0)?.toInt()

        if (min != null) {
             when {
                 min in 0..14 -> selectedMin="15"
                 min in 15..29 -> selectedMin="30"
                 min in 30..44 -> selectedMin="45"
                 min>45 ->{
                     selectedMin="00"
                     selectedHr=(arr[0].toInt()+1).toString()}
             }
         }*/


        dialogTimePicker = Dialog(context, R.style.DialogSlideAnim)
        dialogTimePicker?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogTimePicker?.setContentView(R.layout.dialog_time_picker1)

        val window = dialogTimePicker?.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val params = window?.attributes
        params?.gravity = Gravity.BOTTOM
        window?.attributes = params

        val done = dialogTimePicker?.findViewById(R.id.btn_Done_TimeSelect) as Button
        val cancel = dialogTimePicker?.findViewById(R.id.btn_Cancel_TimeSelect) as Button
        wheelHr = dialogTimePicker?.findViewById(R.id.hr) as WheelPicker
        wheelMin = dialogTimePicker?.findViewById(R.id.min) as WheelPicker
        wheelAmPm = dialogTimePicker?.findViewById(R.id.am_pm) as WheelPicker
        wheelHr.setOnItemSelectedListener(this)
        wheelMin.setOnItemSelectedListener(this)
        wheelAmPm.setOnItemSelectedListener(this)

        wheelHr.data = pickHr.toMutableList()
        wheelMin.data = pickMin.toMutableList()
        wheelAmPm.data = pickAmPm.toMutableList()

        timeSelection(selectedHr,selectedMin,selectedAmPm)





        done.setOnClickListener {
            selectedTime= "$selectedHr:$selectedMin $selectedAmPm"
            onItemClick(selectedTime)
            dialogTimePicker?.dismiss()
        }

        cancel.setOnClickListener {
            dialogTimePicker?.dismiss()
        }

        dialogTimePicker?.show()
    }

    private fun timeSelection(hr: String?,min:String?,amPm:String?) {
        wheelHr.selectedItemPosition = pickHr.indexOf(hr)
        wheelHr.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)

        wheelMin.selectedItemPosition = pickMin.indexOf(min)
        wheelMin.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)


        wheelAmPm.selectedItemPosition = pickAmPm.indexOf(amPm)
        wheelAmPm.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)



    }

    override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
        when (picker) {
            wheelHr -> {
                wheelHr.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
                selectedHr = pickHr[position]
            }
            wheelMin ->{
                wheelMin.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
                selectedMin = pickMin[position]
            }
            wheelAmPm->{
                wheelAmPm.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
                selectedAmPm = pickAmPm[position]
            }

        }


    }
}