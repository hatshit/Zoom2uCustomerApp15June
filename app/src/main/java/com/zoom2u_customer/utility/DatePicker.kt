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
import java.text.SimpleDateFormat
import java.util.*

class DatePicker(): WheelPicker.OnItemSelectedListener {
    private lateinit var mContext: Context
    private var isSelectTimeWindow = 0
    private var dialogTimePicker: Dialog? = null
    private lateinit var wheel_TimeSelect: WheelPicker
    private var selectedDate: String? = null
    var arrayOFDate: MutableList<String> = ArrayList()


    fun datePickerDialog(context: Context, selectedTimeWindowItem: String , onItemClick: (msg: String?) -> Unit) {
        val formatFrom = SimpleDateFormat("EEE dd MMM yyyy")
        for (i in 0..29) {
            var date = Date()
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DATE, i) // add 10 days
            date = cal.time
            val dateStr = formatFrom.format(date)
            arrayOFDate.add(dateStr)
        }


        mContext = context
        isSelectTimeWindow = 0

        dialogTimePicker?.let {
            if (it.isShowing) it.dismiss()
            dialogTimePicker = null
        }

        dialogTimePicker = Dialog(context, R.style.DialogSlideAnim)
        dialogTimePicker!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogTimePicker!!.setContentView(R.layout.dialog_date_picker)

        val window = dialogTimePicker!!.window!!
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val params = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        val done= dialogTimePicker!!.findViewById(R.id.btn_Done_TimeSelect) as Button
        val cancel = dialogTimePicker!!.findViewById(R.id.btn_Cancel_TimeSelect) as Button
        wheel_TimeSelect = dialogTimePicker!!.findViewById(R.id.wheel_TimeSelect) as WheelPicker

        wheel_TimeSelect.setOnItemSelectedListener(this)

        wheel_TimeSelect.data = arrayOFDate
        timeSelection(selectedTimeWindowItem)




        done.setOnClickListener {
            onItemClick(selectedDate)
            dialogTimePicker!!.dismiss()
        }

        cancel.setOnClickListener {
            dialogTimePicker!!.dismiss()
        }

        dialogTimePicker!!.show()
    }


    private fun timeSelection(selectedTimeWindowItem: String) {

        wheel_TimeSelect.selectedItemPosition = arrayOFDate.indexOf(selectedTimeWindowItem)
        wheel_TimeSelect.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
    }

    override fun onItemSelected(picker: WheelPicker?, data: Any?, position: Int) {
        wheel_TimeSelect.selectedItemTextColor = mContext.resources.getColor(R.color.color_cyan)
        selectedDate = arrayOFDate[position]
    }

}