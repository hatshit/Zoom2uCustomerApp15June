package com.zoom2u_customer.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityLogInSignupMainBinding
import com.zoom2u_customer.ui.log_in.LogInActivity
import com.zoom2u_customer.ui.sign_up.SignUpActivity
import com.zoom2u_customer.utility.AppUtility

class LogInSignupMainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityLogInSignupMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppUtility.fullScreenMode(window)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in_signup_main)
        binding.loginWithEmailBtn.setOnClickListener(this)
        binding.signUpBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.loginWithEmailBtn -> {
                val intent = Intent(this, LogInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            R.id.signUpBtn -> {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
        }
    }
}