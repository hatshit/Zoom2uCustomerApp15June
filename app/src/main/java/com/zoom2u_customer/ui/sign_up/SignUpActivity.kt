package com.zoom2u_customer.ui.sign_up

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivitySignUpBinding
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.log_in.LogInActivity
import com.zoom2u_customer.ui.log_in.LoginRequest
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.utility_custom_class.MySpinnerAdapter
import java.lang.reflect.Field


class SignUpActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivitySignUpBinding
    private var categories: MutableList<String> = mutableListOf()
    lateinit var viewModel: SignUpViewModel
    private var repository: SignUpRepository? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        AppUtility.hideKeyboardActivityLunched(this)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)

        try {
            val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup.get(binding.spinner) as ListPopupWindow
            popupWindow.height = 300
        } catch (e: NoClassDefFoundError) {

        } catch (e: ClassCastException) {
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalAccessException) {
        }

        binding.spinner.onItemSelectedListener = this
        categories.add("How did you find us?")
        categories.add("Google")
        categories.add("Radio Ad")
        categories.add("Newspaper")
        categories.add("Received a delivery")
        categories.add("Referral")
        categories.add("Facebook")
        categories.add("Linkedin")
        categories.add("Twitter")
        categories.add("Billboard")
        categories.add("Other")

        binding.spinner.adapter = MySpinnerAdapter(this, categories)

        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        val serviceApi: ServiceApi = com.zoom2u_customer.apiclient.ApiClient.getServices()
        repository = SignUpRepository(serviceApi, this)
        viewModel.repository = repository



        viewModel.getSignupSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                if (it.equals("true")) {
                    val email = binding.email.text.toString()
                    val pass = binding.pass.text.toString()
                    viewModel.getLogin(LoginRequest(username = email, password = pass))
                    Toast.makeText(
                        this,
                        "You have been successfully Registered.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

        viewModel.getLoginSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                if (it.equals("true")) {
                    val intent = Intent(this, BasePageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else
                    DialogActivity.alertDialogSingleButton(
                        this,
                        "Alert!",
                        "You have been successfully Registered please Login."
                    )

            }

        }

        /*binding.parentCl.setOnTouchListener { v, m ->
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            true
        }*/
        binding.signupBtn.setOnClickListener(this)
        binding.signup.setOnClickListener(this)
        binding.findUs.setOnClickListener(this)
        val text =  getString(R.string.term_con1)
        val spannableString = SpannableString(text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=resources.getColor(R.color.base_color)
                ds.underlineColor=resources.getColor(R.color.base_color)
                ds.isUnderlineText = true
            }

            override fun onClick(p0: View) {
                try {
                    val browserIntent: Intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.zoom2u.com.au/customer-terms/")
                    )
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        spannableString.setSpan(clickableSpan, 22, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.termsCon.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.termsCon.movementMethod = LinkMovementMethod.getInstance()

    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.signup_btn -> {
                setSignUpData()
            }
            R.id.signup -> {
                val intent = Intent(this, LogInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                finish()
            }
            R.id.find_us -> {
                binding.spinner.performClick()
            }

        }
    }

    private fun setSignUpData() {
        if (checkValidation(
                binding.firstName.text.toString(),
                binding.lastName.text.toString(),
                binding.email.text.toString().trim(),
                binding.phone.text.toString(),
                binding.pass.text.toString(),
                binding.confirmPass.text.toString(),
                binding.chkTerms.isChecked
            )
        )
            viewModel.getSignUp(
                SignUpRequest(
                    binding.firstName.text.toString(),
                    binding.lastName.text.toString(),
                    binding.company.text.toString(),
                    binding.email.text.toString(),
                    binding.phone.text.toString(),
                    binding.findUs.text.toString(),
                    binding.pass.text.toString(),
                    binding.confirmPass.text.toString(),
                    "true",
                    "0",
                    "Deliveries"
                )
            )
    }


    private fun checkValidation(
        first_name: String,
        last_name: String,
        email: String,
        phone: String,
        pass: String,
        confirmPass: String,
        isChecked: Boolean
    ): Boolean {
        if (first_name == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your first name")
            AppUtility.validateTextField(binding.firstName)
            return false
        } /*else if (!first_name.matches(("[a-zA-Z ]+").toRegex())) {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter alphabets in first name")
            AppUtility.validateTextField(binding.firstName)
            return false
        }*/ else if (last_name == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your last name")
            AppUtility.validateTextField(binding.lastName)
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            return false
        } /*else if (!last_name.matches(("[a-zA-Z ]+").toRegex())) {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter alphabets in last name")
            AppUtility.validateTextField(binding.lastName)
            return false
        }*/ else if (email == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your email")
            AppUtility.validateTextField(binding.email)
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            return false
        } else if (!email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex())) {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter a valid email")
            AppUtility.validateTextField(binding.email)
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            return false
        } else if (phone == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your phone number")
            AppUtility.validateTextField(binding.phone)
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                 AppUtility.removeErrorBackGround(binding.email)
                return false
        } else if (phone.length > 20) {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Please enter valid phone number"
            )
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            AppUtility.validateTextField(binding.phone)
            return false
        } else if (!phone.matches(("^[\\s0-9\\()\\-\\+]+$").toRegex())) {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Please enter valid phone number"
            )
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            AppUtility.validateTextField(binding.phone)
            return false
        } else if (pass == "") {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Password must be at least 6 characters long"
            )
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            if (phone.length <= 20 && phone!="")
                AppUtility.removeErrorBackGround(binding.phone)
            AppUtility.validateTextField(binding.pass)
            return false
        } else if (pass.length < 6) {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Password must be at least 6 characters long"
            )
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            if (phone.length <= 20 && phone!="")
                AppUtility.removeErrorBackGround(binding.phone)
            AppUtility.validateTextField(binding.pass)
            return false
        } else if (confirmPass == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter confirm password")
            AppUtility.validateTextField(binding.confirmPass)
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            if (phone.length <= 20 && phone!="")
                AppUtility.removeErrorBackGround(binding.phone)
            if (pass.length >= 6)
                AppUtility.removeErrorBackGround(binding.pass)
            return false
        } else if (confirmPass != pass) {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Password confirmation and Password must match."
            )
            AppUtility.validateTextField(binding.pass)
            AppUtility.validateTextField(binding.confirmPass)
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            if (phone.length <= 20 && phone!="")
                AppUtility.removeErrorBackGround(binding.phone)


            return false
        } else if (!isChecked) {
            //DialogActivity.alertDialogSingleButton(this, "Alert!", "Please Accept the customer Terms and Conditions.")
            Toast.makeText(
                this,
                "Please Accept the customer Terms and Conditions.",
                Toast.LENGTH_LONG
            ).show()
            if (first_name!="")
                AppUtility.removeErrorBackGround(binding.firstName)
            if (last_name!="")
                AppUtility.removeErrorBackGround(binding.lastName)
            if (email!=""&&email.matches(("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").toRegex()))
                AppUtility.removeErrorBackGround(binding.email)
            if (phone.length <= 20 && phone!="")
                AppUtility.removeErrorBackGround(binding.phone)
            if (confirmPass == pass) {
                AppUtility.removeErrorBackGround(binding.pass)
                AppUtility.removeErrorBackGround(binding.confirmPass)
            }
            return false
        }
        return true
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedText = categories[position]
        if (position == 0)
            binding.findUs.setText("")
        else
            binding.findUs.setText(selectedText)
            binding.whereFindUs.isHintAnimationEnabled = true

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }


}