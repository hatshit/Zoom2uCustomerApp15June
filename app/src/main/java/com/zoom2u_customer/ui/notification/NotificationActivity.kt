package com.zoom2u_customer.ui.notification

import android.app.Activity
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityNotificationBinding
import com.zoom2u_customer.services.MyFcmListenerService
import com.zoom2u_customer.ui.splash_screen.SplashScreenActivity
import com.zoom2u_customer.utility.AppUtility

class NotificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityNotificationBinding
    var msgstr = ""
    var isChatMsg = ""

    lateinit var viewModel: NotificationViewModel
    private var repository: NotificationRepository? = null
    var bookingIDOfNotification = "0"
    var notificationUIVisibilityCount = 0
    private fun showNotificationDialog() {
        val viewGroup = (this as Activity).findViewById<ViewGroup>(R.id.content)

        val dialogView: View =
            LayoutInflater.from(this).inflate(R.layout.default_notificationview, viewGroup, false)

        val builder = AlertDialog.Builder(this)

        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val titleAlert: TextView = dialogView.findViewById(R.id.titleDialog)
        titleAlert.text = "Booking Status!"

        val msgAlertDialog: TextView = dialogView.findViewById(R.id.dialogMessageText)
        msgAlertDialog.text = msgstr

        val crossBtn: ImageView = dialogView.findViewById(R.id.dialogDoneBtn)
        crossBtn.setOnClickListener {
            alertDialog.dismiss()
        }


    }


    @Synchronized
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (getIntent().getStringExtra("bookingId") != null)
            bookingIDOfNotification = intent.getStringExtra("bookingId").toString()
        else if (!MyFcmListenerService.prefrenceForGCM?.getString("bookingId", "0").equals("0")) bookingIDOfNotification =
            MyFcmListenerService.prefrenceForGCM?.getString("bookingId", "0").toString()
        if (bookingIDOfNotification == "0") {
                if (isChatMsg != "")
                    finish()
                else {
                    msgstr = getIntent().getStringExtra("message")!!
                    if (msgstr != "")
                        showNotificationDialog()
                    else
                        finish()
                }
            } else {
                notificationUIVisibilityCount = 1
                initView()
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)


        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = NotificationRepository(serviceApi, this)
        viewModel.repository = repository

        if (!SplashScreenActivity.isMainActivityIsActive) {
            launchMainActivityOnAppKilled()
        } else {
            if (intent.getStringExtra("bookingId") != null)
                bookingIDOfNotification = intent.getStringExtra("bookingId").toString()
            else if (!MyFcmListenerService.prefrenceForGCM?.getString("bookingId", "0").equals("0"))
                bookingIDOfNotification =
                    MyFcmListenerService.prefrenceForGCM?.getString("bookingId", "0").toString()

            if (intent.getStringExtra("chat") != null)
                isChatMsg = intent.getStringExtra("chat").toString()

            clearNotificationFromNotificationBar()



            if (bookingIDOfNotification == "0") {
                if (isChatMsg != "")
                    finish()
                else {
                    msgstr = intent.getStringExtra("message")!!
                    if (msgstr != "")
                        showNotificationDialog()
                    else
                        finish()
                }
            } else {
                super.setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen)

                notificationUIVisibilityCount = 1

                binding.closeNotificatinView.setOnClickListener(View.OnClickListener {
                    notificationUIVisibilityCount = 0
                        finish()
                    })

                binding.thumabsUpBtn.setOnClickListener {
                        viewModel.callServiceToRateCourier(bookingIDOfNotification.toInt(), 1)

                }
                binding.thumabsDownBtn.setOnClickListener {
                        viewModel.callServiceToRateCourier(bookingIDOfNotification.toInt(), -1)
                    }

                initView()

            }
        }



        viewModel.getRateSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                setDataToView(it)
            }
        }

        viewModel.getFeedSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                if(it=="true"){
                finish()
                Toast.makeText(this,"ThankYou for your feedback.",Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun setDataToView(it: RateMeResponse) {
     binding.courierNameHeader.text=it.CourierName.toString()

     binding.notificPickAddressValue.text=it.PickupAddress

     binding.notificDropAddressValue.text=it.DropAddress


        if (!TextUtils.isEmpty(it.CourierImage)) {
            Picasso.get().load(it.CourierImage).into(binding.courierPic)

        }



    }

    @Synchronized
    fun initView() {

        if(!bookingIDOfNotification.isNullOrEmpty())
        viewModel.getRateDetails(bookingIDOfNotification.toInt())

    }


    private fun launchMainActivityOnAppKilled() {
        val launchMainActivity = Intent("android.intent.action.MAIN")
        launchMainActivity.setClass(this@NotificationActivity, SplashScreenActivity::class.java)
        launchMainActivity.action = Intent.ACTION_MAIN
        launchMainActivity.addCategory(Intent.CATEGORY_LAUNCHER)
        if (intent.getStringExtra("bookingId") != null) {
            launchMainActivity.putExtra("message", intent.getStringExtra("message"))
            if (MyFcmListenerService.prefrenceForGCM == null) MyFcmListenerService.prefrenceForGCM =
                getSharedPreferences("bookingId", 0)
            if (MyFcmListenerService.loginEditorForGCM == null) MyFcmListenerService.loginEditorForGCM =
                MyFcmListenerService.prefrenceForGCM?.edit()
            MyFcmListenerService.loginEditorForGCM?.putString(
                "bookingId",
                intent.getStringExtra("bookingId")
            )
            MyFcmListenerService.loginEditorForGCM?.commit()
        } else {
            launchMainActivity.putExtra("message", intent.getStringExtra("message"))
        }
        if (intent.getStringExtra("chat") != null) launchMainActivity.putExtra(
            "chat",
            intent.getStringExtra("chat")
        ) else launchMainActivity.putExtra("chat", intent.getStringExtra("chat"))
        startActivity(launchMainActivity)
        finish()
        clearNotificationFromNotificationBar()
    }

    private fun clearNotificationFromNotificationBar() {
        var notificationManager: NotificationManager? =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancel(0)
        notificationManager = null
    }
}