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

class TimePicker : WheelPicker.OnItemSelectedListener {
    private lateinit var mContext: Context
    private var isSelectTimeWindow = 0
    private var dialogTimePicker: Dialog? = null
    private lateinit var wheelHr: WheelPicker
    private var selectedHr: String = "8:00 AM"
    private val pickTimeArray =
        arrayOf(
            "8:00 AM",
            "8:15 AM",
            "8:30 AM",
            "8:45 AM",
            "9:00 AM",
            "9:15 AM",
            "9:30 AM",
            "9:45 AM",
            "10:00 AM",
            "10:15 AM",
            "10:30 AM",
            "10:45 AM",
            "11:00 AM",
            "11:15 AM",
            "11:30 AM",
            "11:45 AM",
            "12:00 PM",
            "12:15 PM",
            "12:30 PM",
            "12:45 PM",
            "1:00 PM",
            "1:15 PM",
            "1:30 PM",
            "1:45 PM",
            "2:00 PM",
            "2:15 PM",
            "2:30 PM",
            "2:45 PM",
            "3:00 PM",
            "3:15 PM",
            "3:30 PM",
            "3:45 PM",
            "4:00 PM",
            "4:15 PM",
            "4:30 PM",
            "4:45 PM",
            "5:00 PM",
            "5:15 PM",
            "5:30 PM",
            "5:45 PM",
            "6:00 PM",
            "7:00 PM",
            "7:15 PM",
            "7:30 PM",
            "7:45 PM",
            "8:00 PM",
            "8:15 PM",
            "8:30 PM",
            "8:45 PM",
            "9:00 PM",
            "9:15 PM",
            "9:30 PM",
            "9:45 PM",
            "10:00 PM",
            ""
        )
    private val dropTimeArray =
        arrayOf(
            "8:00 AM",
            "8:15 AM",
            "8:30 AM",
            "8:45 AM",
            "9:00 AM",
            "9:15 AM",
            "9:30 AM",
            "9:45 AM",
            "10:00 AM",
            "10:15 AM",
            "10:30 AM",
            "10:45 AM",
            "11:00 AM",
            "11:15 AM",
            "11:30 AM",
            "11:45 AM",
            "12:00 PM",
            "12:15 PM",
            "12:30 PM",
            "12:45 PM",
            "1:00 PM",
            "1:15 PM",
            "1:30 PM",
            "1:45 PM",
            "2:00 PM",
            "2:15 PM",
            "2:30 PM",
            "2:45 PM",
            "3:00 PM",
            "3:15 PM",
            "3:30 PM",
            "3:45 PM",
            "4:00 PM",
            "4:15 PM",
            "4:30 PM",
            "4:45 PM",
            "5:00 PM",
            "5:15 PM",
            "5:30 PM",
            "5:45 PM",
            "6:00 PM",
            "7:00 PM",
            "7:15 PM",
            "7:30 PM",
            "7:45 PM",
            "8:00 PM",
            "8:15 PM",
            "8:30 PM",
            "8:45 PM",
            "9:00 PM",
            "9:15 PM",
            "9:30 PM",
            "9:45 PM",
            "10:00 PM"
        )
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

        dialogTimePicker = Dialog(context, R.style.DialogSlideAnim)
        dialogTimePicker!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogTimePicker!!.setContentView(R.layout.dialog_time_picker)

        val window = dialogTimePicker!!.window!!
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val params = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        val done = dialogTimePicker!!.findViewById(R.id.btn_Done_TimeSelect) as Button
        val cancel = dialogTimePicker!!.findViewById(R.id.btn_Cancel_TimeSelect) as Button
        wheelHr = dialogTimePicker!!.findViewById(R.id.wheel_TimeSelect) as WheelPicker


        wheelHr.setOnItemSelectedListener(this)
        if (isForDrop)
            wheelHr.data = dropTimeArray.toMutableList()
        else
            wheelHr.data = pickTimeArray.toMutableList()
        timeSelection(selectedTimeWindowItem)





        done.setOnClickListener {
            onItemClick(selectedHr)
            dialogTimePicker!!.dismiss()
        }

        cancel.setOnClickListener {
            dialogTimePicker!!.dismiss()
        }

        dialogTimePicker!!.show()
    }

    private fun timeSelection(selectedTimeWindowItem: String) {
        if (isForDropTime)
            wheelHr.selectedItemPosition = dropTimeArray.indexOf(selectedTimeWindowItem)
        else
            wheelHr.selectedItemPosition = pickTimeArray.indexOf(selectedTimeWindowItem)
        wheelHr.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
    }

    override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
        when (picker) {
            wheelHr -> {
                wheelHr.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
                selectedHr = if (isForDropTime)
                    dropTimeArray[position]
                else
                    pickTimeArray[position]
            }

        }


    }
}