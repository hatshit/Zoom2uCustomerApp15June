package com.zoom2u_customer.ui.sign_up

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zoom2u_customer.ui.log_in.LoginRequest

class SignUpViewModel : ViewModel() {
    private var signSuccess: MutableLiveData<String>? = MutableLiveData("")
    private var logSuccess: MutableLiveData<String>? = MutableLiveData("")
    var repository: SignUpRepository? = null

    fun getSignUp(signUpRequest: SignUpRequest) = repository?.getSignUpFromRepo(signUpRequest,onSignupSuccess = ::onSignupSuccess)

    private fun onSignupSuccess(msg:String){
        signSuccess?.value=msg

    }
    fun getSignupSuccess():MutableLiveData<String>?{
        return signSuccess
    }

    fun getLogin(loginRequest: LoginRequest) = repository?.getLoginFromRepo(loginRequest,onLoginSuccess = ::onLoginSuccess)


    private fun onLoginSuccess(msg:String){
        logSuccess?.value=msg

    }


    fun getLoginSuccess():MutableLiveData<String>?{
        return logSuccess
    }

}