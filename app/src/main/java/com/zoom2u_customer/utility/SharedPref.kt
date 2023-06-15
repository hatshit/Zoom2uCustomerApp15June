package com.zoom2u_customer.utility

import com.google.gson.JsonElement
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileResponse
import com.zoom2u_customer.ui.log_in.LoginResponse


interface SharedPref {

    fun setLoginResponse(res: String?)

    fun getLoginResponse(): LoginResponse?

    fun setBrainToken(res: String?)

    fun getBrainToken(): String?

    fun removeLoginResponse()

    fun setProfileData(res: String?)

    fun getProfileData(): ProfileResponse?

    fun setAccountType(res: String?)

    fun getAccountType(): String?
}