package com.zoom2u_customer.ui.log_in

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityLoginSuspendedBinding
import com.zoom2u_customer.ui.log_in.forgot_password.ForgotPasswordActivity
import com.zoom2u_customer.ui.sign_up.SignUpActivity
import com.zoom2u_customer.ui.splash_screen.LogInSignupMainActivity

class LoginSuspendedAct : AppCompatActivity() , View.OnClickListener {
    lateinit var binding:ActivityLoginSuspendedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_suspended)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_suspended)
        binding.logoutBtn.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.logout_btn -> {
                val intent = Intent(this, LogInSignupMainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}