package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.pickup_details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.apiclient.GetAddressFromGoogleAPI
import com.zoom2u_customer.apiclient.GoogleServiceApi
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityPickUpDetailsBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.ContactNameAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.DeliveryDetailsViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.dropoff_details.DropOffDetailsActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.PickUpDetails
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.MyLocationRepository
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq
import com.zoom2u_customer.ui.application.get_location.GetLocationClass
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import java.util.*

class PickUpDetailsActivity : AppCompatActivity(),View.OnClickListener, View.OnTouchListener{
    private var pickAutocompleteRequest = 1019
    var bookDeliveryAlertMsgStr = ""
    lateinit var binding: ActivityPickUpDetailsBinding
    lateinit var viewModel: DeliveryDetailsViewModel
    private var repositoryGoogleAddress: GoogleAddressRepository? = null
    private var repositoryMyLoc: MyLocationRepository? = null
    private var getLocationClass: GetLocationClass? = null
    private var pickState: String? = null
    private var pickStreetNumber: String? = null
    private var pickStreet: String? = null
    private var pickGpx: String? = null
    private var pickGpy: String? = null
    private var pickSuburb: String? = null
    private var pickPostCode: String? = null
    private var pickCountry: String? = null
    private var pickPremisesType: String? = "House"
    private var facilities: String? ="None"
    private lateinit var itemDataList: ArrayList<Icon>
    private var isQuotesRequest: Boolean = false
    private var largeItem: Icon?=null
    private var isflexible:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_up_details)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.pickupView, this)
        /**get data from map Item*/
        val intent: Intent = intent
        if(intent.hasExtra("LargeItem")) {
            largeItem = intent.getParcelableExtra<Icon>("LargeItem")
            if(largeItem?.Value==15||largeItem?.Value==16)  {
                binding.case1.visibility=View.VISIBLE
                binding.case2.visibility=View.GONE
                binding.pickSendSms.visibility = View.GONE
            }else if(largeItem?.Value==24){
                binding.case1.visibility=View.VISIBLE
                binding.case2.visibility=View.VISIBLE
                binding.pickSendSms.visibility = View.GONE
                binding.freightTitleTxt.visibility=View.GONE
                binding.freightNote.visibility=View.GONE
                binding.facilitiesTxt1.visibility=View.GONE
                binding.yesno.visibility=View.GONE
            }
            else{
                binding.case1.visibility=View.GONE
                binding.case2.visibility=View.VISIBLE
            }

        }
        else {
            itemDataList = intent.getParcelableArrayListExtra<Icon>("IconList") as ArrayList<Icon>
            isQuotesRequest = intent.getBooleanExtra("isQuotesRequest", false)
            /**for request quotes*/
            if (isQuotesRequest) {
                binding.pickSendSms.visibility = View.GONE
            } else {
                binding.pickSendSms.visibility = View.VISIBLE
            }

        }





        viewModel = ViewModelProvider(this)[DeliveryDetailsViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        val googleServiceApi: GoogleServiceApi = GetAddressFromGoogleAPI.getGoogleServices()
        repositoryMyLoc = MyLocationRepository(serviceApi, this)
        repositoryGoogleAddress = GoogleAddressRepository(googleServiceApi, this)
        viewModel.repositoryMyLoc = repositoryMyLoc
        viewModel.repositoryGoogleAddress = repositoryGoogleAddress
        viewModel.getMyLocationList(true)
        getLocationClass = GetLocationClass(this)

        binding.pickName.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedContact =
                    parent.adapter.getItem(position) as MyLocationResAndEditLocationReq?
                binding.pickName.setText(selectedContact?.Location?.ContactName)
                if (!selectedContact?.Location?.CompanyName.isNullOrEmpty())
                    binding.pickCompany.setText(selectedContact?.Location?.CompanyName)
                binding.pickEmail.setText(selectedContact?.Location?.Email)
                binding.pickPhone.setText(selectedContact?.Location?.Phone)
                if (!selectedContact?.Location?.UnitNumber.isNullOrEmpty())
                    binding.pickUnit.setText(selectedContact?.Location?.UnitNumber)
                binding.pickInstruction.setText(selectedContact?.Location?.Notes)
                binding.pickAddress.text = selectedContact?.Location?.Street
                pickState = selectedContact?.Location?.State
                pickGpx = selectedContact?.Location?.GPSX.toString()
                pickGpy = selectedContact?.Location?.GPSY.toString()
                pickSuburb = selectedContact?.Location?.Suburb
                pickPostCode = selectedContact?.Location?.Postcode
                pickStreet = selectedContact?.Location?.Street
                pickStreetNumber = selectedContact?.Location?.StreetNumber


            }

        binding.pickHouseCom.setOnCheckedChangeListener { _, checkedId ->
            pickPremisesType = if (R.id.pick_house == checkedId)
                "House"
            else "Commercial"

        }
        binding.facilities.setOnCheckedChangeListener { _, checkedId ->
            facilities =
                when {
                    R.id.forklift == checkedId -> "Forklift"
                    R.id.crane == checkedId -> "Crane"
                    else -> "None"
                }
        }


        viewModel.getMySuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                setDataToContact(it)

            } }


        binding.nextBtn.setOnClickListener(this)
        binding.pickFindMe.setOnClickListener(this)
        binding.pickAddress.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.isNoContactPickup.setOnClickListener(this)
        binding.no.setOnClickListener(this)
        binding.yes.setOnClickListener(this)
        if (!Places.isInitialized()) {
            val apiKey = getString(R.string.google_api_ke)
            Places.initialize(applicationContext, apiKey)
        }

        viewModel.googleSuccessUsingAdd()?.observe(this) { isGoogleAdd ->
            if (isGoogleAdd.isNotEmpty()) {
                AppUtility.progressBarDissMiss()
                val getAddress: HashMap<String, Any>? = isGoogleAdd
                    pickGpx = getAddress?.get("latitude").toString()
                    pickGpy = getAddress?.get("longitude").toString()
                    pickState = getAddress?.get("state")?.toString()
                    pickSuburb = getAddress?.get("suburb")?.toString()
                    pickPostCode = getAddress?.get("postcode")?.toString()
                    pickStreet = getAddress?.get("address")?.toString()
                    pickStreetNumber = getAddress?.get("streetNumber")?.toString()
                    pickCountry = getAddress?.get("country")?.toString()



            }
        }
        viewModel.googleSuccessUsingLatLang()?.observe(this) { isGoogleAdd ->
            if (isGoogleAdd.isNotEmpty()) {
                AppUtility.progressBarDissMiss()
                val getAddress: HashMap<String, Any>? = isGoogleAdd
                    pickGpx = getAddress?.get("latitude").toString()
                    pickGpy = getAddress?.get("longitude").toString()
                    pickState = getAddress?.get("state")?.toString()
                    pickSuburb = getAddress?.get("suburb")?.toString()
                    pickPostCode = getAddress?.get("postcode")?.toString()
                    pickStreet = getAddress?.get("address")?.toString()
                    pickStreetNumber = getAddress?.get("streetNumber")?.toString()
                    pickCountry = getAddress?.get("country")?.toString()



            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setDataToContact(it: List<MyLocationResAndEditLocationReq>) {
        val contactList: MutableList<MyLocationResAndEditLocationReq> = mutableListOf()
        for (myLocation in it) {
            contactList.add(myLocation)
            if (myLocation.DefaultPickup == true) {
                try {
                    binding.pickName.setText(myLocation.Location?.ContactName.toString())
                    binding.pickEmail.setText(myLocation.Location?.Email.toString())
                    binding.pickPhone.setText(myLocation.Location?.Phone.toString())
                    if (!myLocation.Location?.Notes.isNullOrEmpty())
                        binding.pickInstruction.setText(myLocation.Location?.Notes.toString())
                    if (!myLocation.Location?.UnitNumber.isNullOrEmpty())
                        binding.pickUnit.setText(myLocation.Location?.UnitNumber.toString())
                    if (!myLocation.Location?.CompanyName.isNullOrEmpty())
                        binding.pickCompany.setText(myLocation.Location?.CompanyName.toString())
                    binding.pickAddress.text = myLocation.Location?.Street.toString()
                    pickState = myLocation.Location?.State.toString()
                    pickGpx = myLocation.Location?.GPSX.toString()
                    pickGpy = myLocation.Location?.GPSY.toString()
                    pickSuburb = myLocation.Location?.Suburb.toString()
                    pickPostCode = myLocation.Location?.Postcode.toString()
                    pickStreet = myLocation.Location?.Street.toString()
                    pickStreetNumber = myLocation.Location?.StreetNumber.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val pickAdapter = ContactNameAdapter(this, R.layout.autocomplete_text, contactList)
        binding.pickName.threshold = 0
        binding.pickName.setAdapter(pickAdapter)
    }

    override fun onTouch(view: View?, p1: MotionEvent?): Boolean {
        when (view?.id) {
            R.id.pick_address -> {
                pickSearchLocation()
            }
        }
        return true
    }

    private fun pickSearchLocation() {
        try {
            val fields = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG,)
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setCountry("AU")
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityForResult(intent, pickAutocompleteRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
           R.id.no->{
               isflexible=false
               binding.no.setBackgroundResource(R.color.base_color)
               binding.yes.setBackgroundResource(R.color.white)
           }
            R.id.yes->{
                isflexible=true
                binding.no.setBackgroundResource(R.color.white)
                binding.yes.setBackgroundResource(R.color.base_color)
            }
            R.id.back_btn -> {
                finish()
            }
            R.id.is_no_contact_pickup -> {
                if (binding.isNoContactPickup.isChecked)
                    AppUtility.validateEditTextField(
                        binding.pickInstruction,
                        "Notes are required for the pickup location."
                    )
                else {
                    binding.pickInstruction.hint = getString(R.string.instruction)
                    binding.pickInstruction.setHintTextColor(Color.parseColor("#8C9293"))
                }
            }
            R.id.pick_find_me -> {
                getLocationClass?.getCurrentLocation(onAddress = ::getPickAddress)
            }
            R.id.pick_address -> {
                pickSearchLocation()
            }
            R.id.next_btn -> {
                /**check if pick or drop state is TAS*/
                if (pickState == "TAS") {
                    DialogActivity.alertDialogSingleButton(
                        this,
                        "Oops!",
                        "We're sorry,we currently don't service this route.Please contact us if you required more information! You can reach us on  1300 966 628."
                    )
                } else {
                    val isBookingConfirm = validateYourDeliveryField(
                        binding.pickName.text.toString().trim(),
                        binding.pickPhone.text.toString().trim(),
                        binding.pickAddress.text.toString().trim()
                    )
                    if (isBookingConfirm <= 0) {

                        val pickUpDetails = PickUpDetails(binding.pickUnit.text.toString().trim(),
                            pickStreet,
                            binding.pickName.text.toString(),
                            binding.pickPhone.text.toString(),
                            binding.pickEmail.text.toString(),
                            binding.pickAddress.text.toString(),
                            binding.pickInstruction.text.toString(),
                            pickGpx, pickGpy, pickStreetNumber,
                            pickSuburb,pickState, pickPostCode,
                            pickPremisesType,
                            binding.pickCompany.text.toString(), pickCountry,
                            binding.pickSendSms.isChecked,
                            binding.isNoContactPickup.isChecked,
                            binding.freightNote.text.toString(),
                            facilities
                        )

                        if(intent.hasExtra("LargeItem")){
                            val intent = Intent(this, DropOffDetailsActivity::class.java)
                            intent.putExtra("PickUpDetails", pickUpDetails)
                            intent.putExtra("LargeItem", largeItem)
                            //intent.putExtra("isQuotesRequest", isQuotesRequest)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }else {
                            val intent = Intent(this, DropOffDetailsActivity::class.java)
                            intent.putExtra("PickUpDetails", pickUpDetails)
                            intent.putParcelableArrayListExtra("IconList", itemDataList)
                            intent.putExtra("isQuotesRequest", isQuotesRequest)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                    else
                        DialogActivity.alertDialogSingleButton(this, "Awaiting!", bookDeliveryAlertMsgStr)
                }
            }
        }
    }



    private fun getPickAddress(lat: Double, lang: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lang, 1)
        binding.pickAddress.text = address[0].getAddressLine(0)
        viewModel.addFromGoogleLatLang(lat.toString(), lang.toString(), true)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickAutocompleteRequest) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    binding.pickAddress.text = place.address
                    viewModel.addFromGoogleAdd(place.address, true)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    binding.pickAddress.text = ""
                }
                RESULT_CANCELED -> {
                    Log.i("Place API Failure", "  -------------- User Cancelled -------------")
                }
            }
        }

    }

    private fun validateYourDeliveryField(
        pickName: String,
        pickPhone: String,
        pickAddress: String): Int {
        var bookDeliveryAlertCount = 0
        bookDeliveryAlertMsgStr = ""


        /******************* Validation for pick up detail ******************/
        if (pickName == ""
            || pickPhone == ""
            || pickAddress == ""
            || !pickPhone.matches(("^[\\s0-9\\()\\-\\+]+$").toRegex())
        ) {
            if (pickName == "") {
                AppUtility.validateEditTextField(binding.pickName, "Please enter contact name.")
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at pickup name.", bookDeliveryAlertCount)
            }
            if (pickPhone == "") {
                AppUtility.validateEditTextField(binding.pickPhone, "Please enter mobile number.")
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at pickup's number.", bookDeliveryAlertCount)
            } else if (!pickPhone.matches(("^[\\s0-9\\()\\-\\+]+$").toRegex())) {

                AppUtility.validateEditTextField(
                    binding.pickPhone,
                    "Please enter valid mobile number."
                )
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at pickup's number.", bookDeliveryAlertCount)
            }
            if (pickAddress == "") {
                AppUtility.validateTextField(binding.pickAddress, "Please enter address.")
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at pickup's address.", bookDeliveryAlertCount)
            }

        }


        /**for no contact pickup*/
        if (binding.isNoContactPickup.isChecked) {
            if (binding.pickInstruction.text.toString() == "") {
                AppUtility.validateEditTextField(
                    binding.pickInstruction,
                    "Notes are required for the pickup location."
                )
                bookDeliveryAlertCount++
                addTextToAlertDialog(
                    "Notes are required for the pickup location when requesting a no contact delivery.",
                    bookDeliveryAlertCount
                )
            }
        }


        return bookDeliveryAlertCount
    }

    private fun addTextToAlertDialog(addAlertMsgStr: String, count: Int) {
        if (bookDeliveryAlertMsgStr == "") bookDeliveryAlertMsgStr =
            "$bookDeliveryAlertMsgStr$count) $addAlertMsgStr" else bookDeliveryAlertMsgStr =
            "$bookDeliveryAlertMsgStr\n$count) $addAlertMsgStr"

    }

    override fun onBackPressed() {
        if(intent.hasExtra("LargeItem"))
        DialogActivity.logoutDialog(
            this,
            "Abandon this booking?",
            "This booking's information will be lost if you cancel.",
            "Continue", "Abandon",
            onCancelClick = ::onCancelClick,
            onOkClick = ::onOkClick
        )else
            finish()
    }

    private fun onCancelClick() {
        val intent = Intent()
        setResult(12, intent)
        finish()
    }

    private fun onOkClick() {}
}