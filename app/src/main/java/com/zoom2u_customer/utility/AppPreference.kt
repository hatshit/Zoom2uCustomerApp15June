package com.zoom2u_customer.utility

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileResponse
import com.zoom2u_customer.ui.log_in.LoginResponse

object AppPreference : SharedPref {

    private val Preference_name :String = "zoom2u_pref"

    private var sp: SharedPreferences?=null

    private var editor: SharedPreferences.Editor? = null

    private val Login_Response :String= "login_response"

    private  val Profile:String= "Profile"

    private  val Account:String= "Account"

    private  val Brain_Token:String= "Brain_Token"


    fun getSharedPrefInstance(): SharedPref {
            return this
        }

    init {
        sp = Zoom2u.getInstance()?.getSharedPreferences(Preference_name, Context.MODE_PRIVATE)
        editor = sp?.edit()
    }

    override fun setLoginResponse(res: String?) {
            editor?.putString(Login_Response, res)
            editor?.commit()
        }


    override fun getLoginResponse(): LoginResponse? {
        val gson = Gson()
        return gson.fromJson(sp?.getString(Login_Response, null), LoginResponse::class.java)
    }

    override fun setBrainToken(res: String?) {
        editor?.putString(Brain_Token, res)
        editor?.commit()
    }


    override fun getBrainToken(): String? {
        return sp?.getString(Brain_Token, null)
    }

    override fun removeLoginResponse() {
        editor?.remove(Login_Response)
        editor?.commit()
    }

    override fun setProfileData(res: String?) {
        editor?.putString(Profile, res)
        editor?.commit()
    }

    override fun getProfileData(): ProfileResponse? {
        val gson = Gson()
        return gson.fromJson(sp?.getString(Profile, null), ProfileResponse::class.java)
    }

    override fun setAccountType(res: String?) {
        editor?.putString(Account, res)
        editor?.commit()
    }

    override fun getAccountType(): String? {
        return sp?.getString(Account, null)
    }
}