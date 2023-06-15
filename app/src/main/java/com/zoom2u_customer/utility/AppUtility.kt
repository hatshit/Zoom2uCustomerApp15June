package com.zoom2u_customer.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.zoom2u_customer.R
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileResponse
import com.zoom2u_customer.ui.log_in.LoginResponse
import com.zoom2u_customer.ui.splash_screen.LogInSignupMainActivity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class AppUtility {
    companion object {



        fun onLogoutCall(context: Context?){
            val loginResponse: LoginResponse? = AppPreference.getSharedPrefInstance().getLoginResponse()
            loginResponse?.access_token = ""
            AppPreference.getSharedPrefInstance().setLoginResponse(Gson().toJson(loginResponse))

            val profileResponse: ProfileResponse? = AppPreference.getSharedPrefInstance().getProfileData()
            profileResponse?.FirstName = ""
            AppPreference.getSharedPrefInstance().setProfileData(Gson().toJson(profileResponse))


            val intent = Intent(context, LogInSignupMainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context?.startActivity(intent)
            (context as Activity).finish()
        }


        fun Bitmap.toSquare():Bitmap?{

            val side = width.coerceAtMost(height)
            val xOffset = (width - side) /2
            val yOffset = (height - side)/2

            return Bitmap.createBitmap(this, xOffset, yOffset, side, side)
        }







    /**for password show hide*/
       fun showHidePassword(button: Button){
         //  if(button.isActivated)
               //ma
       }

        fun getDeviceWight():Int?{
            val displayMetrics: DisplayMetrics? = Zoom2u.getInstance()?.resources?.getDisplayMetrics()
            return displayMetrics?.widthPixels
        }
        fun getDeviceHeight():Int?{
            val displayMetrics: DisplayMetrics? = Zoom2u.getInstance()?.resources?.getDisplayMetrics()
            return displayMetrics?.heightPixels
        }



        var progressDialog: ProgressDialog? = null


        var emailPattern = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")


        fun validateTextField(validateTxtField: TextInputEditText) {
            validateTxtField.setBackgroundResource(R.drawable.blankbox)
        }


        fun removeErrorBackGround(validateTxtField: TextInputEditText) {
            validateTxtField.setBackgroundResource(R.drawable.text_background)
        }



        fun progressBarShow(context: Context?) {
            progressDialog = ProgressDialog(context as Activity, R.style.progressbarstyle)
            progressDialog?.setMessage(
                "Loading" + "..."
            )
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog?.isIndeterminate = true
            progressDialog?.show()
        }


        fun progressBarDissMiss() {
            try {
                progressDialog?.dismiss()
                progressDialog?.setCancelable(false)
                progressDialog?.window
                    ?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun getApiHeaders(): Map<String, String> {
            val headers: MutableMap<String, String> = HashMap()
            val loginResponse: LoginResponse? =
                AppPreference.getSharedPrefInstance().getLoginResponse()
            headers["authorization"] = "Bearer" + " " + loginResponse?.access_token
            return headers
        }


        fun isInternetConnected(): Boolean {
            val cm = Zoom2u.getInstance()
                ?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun fullScreenMode(window: Window) {
            val currentApiVersion = Build.VERSION.SDK_INT

            val flags: Int = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT)
                window.decorView.systemUiVisibility = flags
        }


        fun getJsonObject(params: String?): JsonObject? {
            val parser = JsonParser()
            return parser.parse(params).asJsonObject
        }

        fun hideKeyboardActivityLunched(activity:Activity) {
            activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }


        fun hideKeyBoardClickOutside(view:View, context: Context){
            view.setOnTouchListener { _, _ ->
                val imm: InputMethodManager =
                    context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow((context as Activity).currentFocus?.windowToken, 0)
                true
            }
        }

        fun upperCaseFirst(`val`: String): String {
            val arr = `val`.toCharArray()
            arr[0] = Character.toUpperCase(arr[0])
            return String(arr)
        }



        fun validateEditTextField(validateTxtField: EditText, msgStr: String?) {
            validateTxtField.hint = msgStr
            validateTxtField.setHintTextColor(Color.parseColor("#FF476A"))

        }
        fun validateTextField(validateTxtField: TextView, msgStr: String?) {
            validateTxtField.hint = msgStr
            validateTxtField.setHintTextColor(Color.parseColor("#FF476A"))

        }

        fun getDateTimeFromDeviceToServerForDate(serverDateTimeValue: String?): String? {
            val dateTimeReturn: String? = null
            try {
                if (serverDateTimeValue != "") {
                    val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    converter.timeZone = TimeZone.getTimeZone("GMT")
                    var convertedDate: Date? = Date()
                    try {
                        convertedDate = converter.parse(serverDateTimeValue)
                        val dateFormatter = SimpleDateFormat("dd-MMM-yy hh:mm a")
                        return dateFormatter.format(convertedDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return dateTimeReturn
        }



        fun getDateTime(serverDateTimeValue: String?): Date {
            var convertedDate=Date()
            try {
                if (serverDateTimeValue != "") {
                    val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    converter.timeZone = TimeZone.getTimeZone("GMT")
                    try {
                        convertedDate = converter.parse(serverDateTimeValue)
                        return convertedDate
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return convertedDate
        }



        fun getBitmapFromURL(imagePath: String?) : Bitmap?{
            var bitmap: Bitmap?=null
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
                val url = URL(imagePath)
                bitmap= BitmapFactory.decodeStream(url.content as InputStream)
            } catch (e: IOException) {

            }
            return bitmap
        }


        fun fullSizeImageView(context: Context?,photo:String){
            val intent = Intent(context, FullSizeImageActivity::class.java)
            intent.putExtra("Photo",photo)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            context?.startActivity(intent)
        }



        fun getAccountType():String{
            val profileResponse: ProfileResponse? = AppPreference.getSharedPrefInstance().getProfileData()
            return profileResponse?.AccountType.toString()
        }


      /**get current time ETA*/
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateAndTimeInEta(): String {
            val date = Date()
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")

            return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
                dateFormat.format(date) + " " +
                        timeFormat.format(date)
            ).toString()

        }


        fun getPickupDateAndTimeEnd(): String {
            val date = Date()
            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val currentTime = timeFormat.format(date)
            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
            val time = Calendar.getInstance()
            time.add(Calendar.HOUR, 4)
            val d = time.time
            val dropDate = if (timeFormat.parse(timeFormat.format(d)).before(currTimeInDateFormat)) {
                val c = Calendar.getInstance()
                c.add(Calendar.DATE, 1)
                val date1 = c.time
                dateFormat.format(date1)
            } else
                dateFormat.format(date)

            return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
                dropDate + " " +
                        timeFormat.format(d)
            ).toString()

        }


        fun getDropOffDateAndTimeStart(): String {
            val date = Date()
            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val currentTime = timeFormat.format(date)
            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
            val time = Calendar.getInstance()
            time.add(Calendar.HOUR, 5)
            val d = time.time
            val dropDate = if (timeFormat.parse(timeFormat.format(d)).before(currTimeInDateFormat)) {
                val c = Calendar.getInstance()
                c.add(Calendar.DATE, 1)
                val date1 = c.time
                dateFormat.format(date1)
            } else
                dateFormat.format(date)

            return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
                dropDate + " " +
                        timeFormat.format(d)
            ).toString()

        }


        fun getDropOffDateAndTimeEnd(): String {
            val date = Date()
            val timeFormat: DateFormat = SimpleDateFormat("hh:mm aaa")
            val dateFormat: DateFormat = SimpleDateFormat("EEE dd MMM yyyy")
            val currentTime = timeFormat.format(date)
            val currTimeInDateFormat: Date = timeFormat.parse(currentTime)
            val time = Calendar.getInstance()
            time.add(Calendar.HOUR, 9)
            val d = time.time
            val dropDate = if (timeFormat.parse(timeFormat.format(d)).before(currTimeInDateFormat)) {
                val c = Calendar.getInstance()
                c.add(Calendar.DATE, 1)
                val date1 = c.time
                dateFormat.format(date1)
            } else
                dateFormat.format(date)

            return DateTimeUtil.getDateTimeFromDeviceForDeliveryETA(
                dropDate + " " +
                        timeFormat.format(d)
            ).toString()


        }



        fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                "Zoom2u"+ Calendar.getInstance().time,
                null
            )
            return Uri.parse(path)
        }


        fun isAppOnForeground(context: Context?): Boolean {
            val activityManager =
                context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            for (appProcess in appProcesses) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == "com.zoom2u_customer") {
                    return true
                }
            }
            return false
        }

    }
}