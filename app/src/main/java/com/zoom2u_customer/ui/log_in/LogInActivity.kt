package com.zoom2u_customer.ui.log_in

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityLogInBinding
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.log_in.forgot_password.ForgotPasswordActivity
import com.zoom2u_customer.ui.sign_up.SignUpActivity
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity


class LogInActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityLogInBinding
    lateinit var viewModel: LoginViewModel
    private var repository: LoginRepository? = null
    private var email: String? = null
    private var pass: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl,this)
        AppUtility.hideKeyBoardClickOutside(binding.cl1,this)
        AppUtility.hideKeyboardActivityLunched(this)
        binding.loginBtn.setOnClickListener(this)
        binding.register.setOnClickListener(this)
        binding.forgetPass.setOnClickListener(this)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = LoginRepository(serviceApi,this)
        viewModel.repository = repository

        viewModel.getLoginSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                if (it.equals("true")) {
                    val intent = Intent(this, BasePageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else
                    DialogActivity.alertDialogSingleButton(this, "Alert!", it)

            }
        }

       /* binding.emailHeader.isF
        binding.scrollView.scrollTo(0,50)
*/


    }





    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.login_btn -> {
                setLoginData()

            }
            R.id.register -> {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.forget_pass -> {
                val intent = Intent(this, ForgotPasswordActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
    }


    private fun setLoginData() {

        email = binding.email.text.toString()
        pass = binding.pass.text.toString()

        if (SetValidation(binding.email.text.toString().trim(), binding.pass.text.toString())) {
            viewModel.getLogin(LoginRequest(username = email!!, password = pass!!))
        }


    }



    private fun SetValidation(email: String, pass: String): Boolean {
        binding.scrollView.fullScroll(View.FOCUS_DOWN)
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(pass)) {
            AppUtility.validateTextField(binding.email)
            AppUtility.validateTextField(binding.pass)
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter valid email address and password")
            return false
        } else{
            if (TextUtils.isEmpty(email)) {
                DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your email address")
                AppUtility.validateTextField(binding.email)
                if (pass!="")
                    AppUtility.removeErrorBackGround(binding.pass)
                return false
            }
            else if (!email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())) {
                AppUtility.validateTextField(binding.email)
                DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter a valid email")
                if (pass!="")
                    AppUtility.removeErrorBackGround(binding.pass)
                return false
            }
            else if (TextUtils.isEmpty(pass)) {
                DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your password")
                AppUtility.validateTextField(binding.pass)
                if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                    AppUtility.removeErrorBackGround(binding.email)
                return false
            }
        }
        AppUtility.removeErrorBackGround(binding.email)
        AppUtility.removeErrorBackGround(binding.pass)
            return true

    }
    private fun String.matches(emailRegix: String): Boolean {
        return true;
    }


    override fun onBackPressed() {
        super.onBackPressed()
        AppUtility.progressBarDissMiss()
        finish()
    }


}
