package com.zoom2u_customer.ui.application.bottom_navigation.profile.chnage_password

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityChnagePassBinding
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity


open class ChangePassActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityChnagePassBinding
    lateinit var viewModel: ChangePassViewModel
    private var repository: ChangePassRepository? = null
    var bookDeliveryAlertMsgStr = ""
    private var oldPasswordStr: String? = null
    private var newPasswordStr: String? = null
    private var confirmNewPassStr: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chnage_pass)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl,this)
        AppUtility.hideKeyBoardClickOutside(binding.scrollView,this)
        AppUtility.hideKeyboardActivityLunched(this)
        binding.changePassBtn.setOnClickListener(this)
       binding.zoom2uHeader.backBtn.setOnClickListener(this)

        viewModel = ViewModelProvider(this).get(ChangePassViewModel::class.java)
        val serviceApi: ServiceApi = com.zoom2u_customer.apiclient.ApiClient.getServices()
        repository = ChangePassRepository(serviceApi, this)
        viewModel.repository = repository

        viewModel.getChangePassSuccess()?.observe(this){
            if(!it.isNullOrEmpty()){
                AppUtility.progressBarDissMiss()
                finish()
                Toast.makeText(this,"Your password updated successfully.",Toast.LENGTH_LONG).show()
            }
        }


    }




    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.change_pass_btn -> {
                hideKeyboard(this)
                oldPasswordStr = binding.currPass.text.toString()
                newPasswordStr = binding.newPass.text.toString()
                confirmNewPassStr = binding.cnfrmPass.text.toString()
                if (oldPasswordStr == "" || newPasswordStr == "" || confirmNewPassStr == "") {
                    checkValidationForEmptyField()
                } else if (newPasswordStr!!.length < 6 || newPasswordStr != confirmNewPassStr) {
                    checkValidationForNonEmptyField()
                } else {
                    viewModel.changePass(
                        binding.currPass.text.toString(),
                        binding.cnfrmPass.text.toString()
                    )
                }

            }
            R.id.back_btn->{
                finish()
            }

        }
    }


    /************ Check validation for Non empty fields  */
    private fun checkValidationForNonEmptyField() {
        var alertCount = 0
        var alertMsg = ""
        if (newPasswordStr?.length!! < 6 && newPasswordStr != confirmNewPassStr) {
            AppUtility.validateTextField(binding.newPass)
            AppUtility.validateTextField(binding.cnfrmPass)
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "1) Password must be atleast 6 characters long\n2) Please confirm your new password"
            )
        } else {
            if (newPasswordStr?.length!! < 6) {
                alertCount++
                AppUtility.validateTextField(binding.currPass)
                alertMsg = "$alertMsg$alertCount) Password must be atleast 6 characters long."
            }
            if (newPasswordStr != confirmNewPassStr) {
                alertCount++
                AppUtility.validateTextField(binding.currPass)
                alertMsg =
                    if (alertMsg == "") "$alertMsg$alertCount) Password and confirm password do not match." else "$alertMsg\n$alertCount) Password and confirm Password mismatch"
            }
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                alertMsg
            )
        }
    }

    /********************  Check validation for empty fields   */
    fun checkValidationForEmptyField() {
        if (oldPasswordStr == "" && newPasswordStr == "" && confirmNewPassStr == "") {
            AppUtility.validateTextField(binding.currPass)
            AppUtility.validateTextField(binding.cnfrmPass)
            AppUtility.validateTextField(binding.newPass)
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "1) Please enter your current password.\n2) Password must be atleast 6 characters long.\n3) Password and confirm password do not match."
            )
        } else {
            var alertCount = 0
            var alertMsg = ""
            if (oldPasswordStr == "") {
                alertCount++
                AppUtility.validateTextField(binding.currPass)
                alertMsg = "$alertMsg$alertCount) Please enter your current password"
            }
            if (newPasswordStr == "") {
                alertCount++
                AppUtility.validateTextField(binding.newPass)
                alertMsg = if (alertMsg == "")
                    "$alertMsg$alertCount) Password must be atleast 6 characters long."
                else
                    "$alertMsg\n$alertCount) Password must be atleast 6 characters long."


            }
            if (confirmNewPassStr == "") {
                alertCount++
                AppUtility.validateTextField(binding.cnfrmPass)
                alertMsg = if (alertMsg == "")
                    "$alertMsg$alertCount) Password and confirm password do not match."
                else
                    "$alertMsg\n$alertCount) Password and confirm password do not match."

            }
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                alertMsg
            )
        }
    }

    fun hideKeyboard(context: Context) {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = (context as Activity).currentFocus ?: return
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }
}