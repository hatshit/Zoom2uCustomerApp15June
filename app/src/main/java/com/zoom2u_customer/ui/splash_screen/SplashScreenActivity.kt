package com.zoom2u_customer.ui.splash_screen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageRepository
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileRepository
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility

class SplashScreenActivity : AppCompatActivity() {
    private var profileRepository: ProfileRepository? = null
    private var repository: BasePageRepository? = null
    lateinit var viewModel: BasePageViewModel
    companion object {
        var gcmTokenID: String? = null
        var isMainActivityIsActive = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh_screen)
        viewModel = ViewModelProvider(this)[BasePageViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = BasePageRepository(serviceApi, this)
        viewModel.repository = repository
        viewModel.profileRepository = profileRepository
        isMainActivityIsActive = true
        AppUtility.fullScreenMode(window)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        viewModel.getProfile()
        Handler(Looper.getMainLooper()).postDelayed({
            //viewModel.getProfile()
            if (AppPreference.getSharedPrefInstance().getLoginResponse()!=null) {
                if (TextUtils.isEmpty(AppPreference.getSharedPrefInstance().getLoginResponse()?.access_token)) {
                    val intent = Intent(this@SplashScreenActivity, LogInSignupMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else {
                    LoginSuccessFully()
                }
            }else{
                val intent = Intent(this@SplashScreenActivity, LogInSignupMainActivity::class.java)
                startActivity(intent)
                finish()
            }

            finish()
        }, 3000)



    }
 private fun LoginSuccessFully() {
     startActivity(Intent(this@SplashScreenActivity , BasePageActivity:: class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
     finish()
    }




}




