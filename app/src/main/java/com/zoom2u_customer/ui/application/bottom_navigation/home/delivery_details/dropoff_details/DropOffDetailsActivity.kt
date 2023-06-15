package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.dropoff_details

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.apiclient.GetAddressFromGoogleAPI
import com.zoom2u_customer.apiclient.GoogleServiceApi
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityDropOffDetailsBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.AvailabilityDeliveryActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.availability_delivery_heavy.AvailabilityDeliveryHeavyFreightPickDateActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.ContactNameAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.DeliveryDetailsViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid.ItemHeavyFreight
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.*
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.MyLocationRepository
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq
import com.zoom2u_customer.ui.application.get_location.GetLocationClass
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.utility_custom_class.MySpinnerAdapter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DropOffDetailsActivity : AppCompatActivity() , View.OnClickListener, View.OnTouchListener,
    AdapterView.OnItemSelectedListener{
    private var dropAutocompleteRequest = 1018
    private var pickUpDetails: PickUpDetails? = null
    private var intraStateReq: IntraStateReq? = null
    private var interStateReq: InterStateReq? = null
    var bookDeliveryAlertMsgStr = ""
    lateinit var binding: ActivityDropOffDetailsBinding
    private var facilities: String? ="None"
    private var facilitiesPick: String? ="None"
    lateinit var viewModel: DeliveryDetailsViewModel
    private var repositoryGoogleAddress: GoogleAddressRepository? = null
    private var repositoryMyLoc: MyLocationRepository? = null
    private var getLocationClass: GetLocationClass? = null
    private var dropState: String? = null
    private var dropStreetNumber: String? = null
    private var dropStreet: String? = null
    private var dropGpx: String? = null
    private var dropGpy: String? = null
    private var dropSuburb: String? = null
    private var dropPostCode: String? = null
    private var dropCountry: String? = null
    private var dropPremisesType: String? = "House"
    private var leaveAt: Int? = null
    private var isInterstate: Boolean? = null
    private var isLaptopOrMobile: String? = "laptopOrMobileNo"
    private var IsLaptopOrMobileChecked: Boolean? = false
    private lateinit var itemDataList: ArrayList<Icon>
    private var isQuotesRequest: Boolean = false
    private var categories: MutableList<String> = mutableListOf()
    private var largeItem: Icon?=null
    private var isflexible:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_drop_off_details)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.dropView, this)
        if (intent.hasExtra("IconList")) {
            pickUpDetails = intent.getParcelableExtra("PickUpDetails")
            itemDataList = intent.getParcelableArrayListExtra<Icon>("IconList") as ArrayList<Icon>
            isQuotesRequest = intent.getBooleanExtra("isQuotesRequest", false)
        }else if(intent.hasExtra("LargeItem")){
            largeItem = intent.getParcelableExtra("LargeItem")
            pickUpDetails = intent.getParcelableExtra("PickUpDetails")
            if(largeItem?.Value==15||largeItem?.Value==16)  {
                binding.case1.visibility=View.VISIBLE
                binding.case2.visibility=View.GONE
            }else if(largeItem?.Value==24){
                binding.case1.visibility=View.VISIBLE
                binding.case2.visibility=View.VISIBLE
                binding.freightTitleTxt.visibility = View.GONE
                binding.freightNote.visibility = View.GONE
                binding.facilitiesTxt1.visibility = View.GONE
                binding.yesNo1.visibility = View.GONE
            }
                else{
                binding.case1.visibility=View.GONE
                binding.case2.visibility=View.VISIBLE
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


        categories.add("Front door")
        categories.add("Back door")
        categories.add("Side door")
        categories.add("Other")
        binding.spinner.adapter = MySpinnerAdapter(this, categories)


        getLocationClass = GetLocationClass(this)
        binding.spinner.onItemSelectedListener = this
        binding.authorityTo.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.itemWeNotSend.setOnClickListener(this)
        binding.dropFindMe.setOnClickListener(this)
        binding.dropAddress.setOnClickListener(this)
        binding.noContactDrop.setOnClickListener(this)
        binding.authorityToLeave.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.no1.setOnClickListener(this)
        binding.yes1.setOnClickListener(this)
        if (!Places.isInitialized()) {
            val apiKey = getString(R.string.google_api_ke)
            Places.initialize(applicationContext, apiKey)
        }

        binding.dropName.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                val selectedContact =
                    parent.adapter.getItem(position) as MyLocationResAndEditLocationReq?
                binding.dropName.setText(selectedContact?.Location?.ContactName)
                if (!selectedContact?.Location?.CompanyName.isNullOrEmpty())
                    binding.dropCompany.setText(selectedContact?.Location?.CompanyName)
                binding.dropEmail.setText(selectedContact?.Location?.Email)
                binding.dropPhone.setText(selectedContact?.Location?.Phone)
                if (!selectedContact?.Location?.UnitNumber.isNullOrEmpty())
                    binding.dropUnit.setText(selectedContact?.Location?.UnitNumber)
                binding.dropInstruction.setText(selectedContact?.Location?.Notes)
                binding.dropAddress.text = selectedContact?.Location?.Street
                dropState = selectedContact?.Location?.State
                showHideWeight(pickUpDetails?.pickState, dropState)
                dropGpx = selectedContact?.Location?.GPSX.toString()
                dropGpy = selectedContact?.Location?.GPSY.toString()
                dropSuburb = selectedContact?.Location?.Suburb
                dropPostCode = selectedContact?.Location?.Postcode
                dropStreet = selectedContact?.Location?.Street
                dropStreetNumber = selectedContact?.Location?.StreetNumber
                showErrorForSamePickAndDropAddress()
            }


        viewModel.getMySuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                setDataToContact(it)
            }
        }


        viewModel.googleSuccessUsingAdd()?.observe(this) { isGoogleAdd ->
            if (isGoogleAdd.isNotEmpty()) {
                AppUtility.progressBarDissMiss()
                val getAddress: HashMap<String, Any>? = isGoogleAdd

                    dropGpx = getAddress?.get("latitude").toString()
                    dropGpy = getAddress?.get("longitude").toString()
                    dropState = getAddress?.get("state")?.toString()
                    showHideWeight(pickUpDetails?.pickState, dropState)
                    dropSuburb = getAddress?.get("suburb")?.toString()
                    dropPostCode = getAddress?.get("postcode")?.toString()
                    dropStreet = getAddress?.get("address")?.toString()
                    dropStreetNumber = getAddress?.get("streetNumber")?.toString()
                    dropCountry = getAddress?.get("country")?.toString()
                    showErrorForSamePickAndDropAddress()
            }
        }
        viewModel.googleSuccessUsingLatLang()?.observe(this) { isGoogleAdd ->
            if (isGoogleAdd.isNotEmpty()) {
                AppUtility.progressBarDissMiss()
                val getAddress: HashMap<String, Any>? = isGoogleAdd
                    dropGpx = getAddress?.get("latitude").toString()
                    dropGpy = getAddress?.get("longitude").toString()
                    dropState = getAddress?.get("state")?.toString()
                    showHideWeight(pickUpDetails?.pickState, dropState)
                    dropSuburb = getAddress?.get("suburb")?.toString()
                    dropPostCode = getAddress?.get("postcode")?.toString()
                    dropStreet = getAddress?.get("address")?.toString()
                    dropStreetNumber = getAddress?.get("streetNumber")?.toString()
                    dropCountry = getAddress?.get("country")?.toString()
                    showErrorForSamePickAndDropAddress()
            }
        }

        binding.dropHouseCom.setOnCheckedChangeListener { _, checkedId ->
            dropPremisesType = if (R.id.drop_house == checkedId)
                "House"
            else "Commercial"

        }

        binding.yesNo.setOnCheckedChangeListener { _, checkedId ->
            isLaptopOrMobile = if (R.id.yes == checkedId){
                "laptopOrMobileYes"
            }
            else "laptopOrMobileNo"
            IsLaptopOrMobileChecked= if (R.id.yes == checkedId){
                true
            }
            else false
        }

        binding.facilities.setOnCheckedChangeListener { _, checkedId ->
            facilities =
                when {
                    R.id.forklift == checkedId -> "Forklift"
                    R.id.crane == checkedId -> "Crane"
                    else -> "None"
                }
        }

        binding.facilitiesPick.setOnCheckedChangeListener { _, checkedId ->
            facilitiesPick =
                when {
                    R.id.forklift_pick == checkedId -> "Forklift"
                    R.id.crane_pick == checkedId -> "Crane"
                    else -> "None"
                }
        }

    }

    private fun showHideWeight(pickState: String?, dropState: String?) {
        if (pickState != null && dropState != null) {
            if (isQuotesRequest || (pickState == "TAS" || dropState == "TAS")) {
                binding.packageType.visibility = View.GONE
                binding.yesNo.visibility = View.GONE
                binding.itemWeNotSend.visibility = View.GONE
              if(isQuotesRequest){
                  if (pickState != dropState) {
                      binding.case2.visibility = View.VISIBLE
                      binding.facilitiesTxtPick.visibility=View.VISIBLE
                      binding.facilitiesPick.visibility=View.VISIBLE
                      binding.facilitiesTxt.text = "What loading facilities will be available?*(for dropoff)"
                      binding.freightTitleTxt.visibility = View.GONE
                      binding.freightNote.visibility = View.GONE
                      binding.facilitiesTxt1.visibility = View.GONE
                      binding.yesNo1.visibility = View.GONE
                  } else {
                      binding.case2.visibility = View.GONE
                  }
              }

            } else {
                if(intent.hasExtra("LargeItem")){
                    if(largeItem?.Value==15||largeItem?.Value==16||largeItem?.Value==24) {
                        if (pickState != dropState) {
                            binding.case2.visibility = View.VISIBLE
                            binding.facilitiesTxtPick.visibility=View.VISIBLE
                            binding.facilitiesPick.visibility=View.VISIBLE
                            binding.facilitiesTxt.text = "What loading facilities will be available?*(for dropoff)"
                            binding.freightTitleTxt.visibility = View.GONE
                            binding.freightNote.visibility = View.GONE
                            binding.facilitiesTxt1.visibility = View.GONE
                            binding.yesNo1.visibility = View.GONE
                        } else {
                            binding.case2.visibility = View.GONE
                        }
                    }

                }
                else {
                if (pickState != dropState) {
                        binding.packageType.visibility = View.VISIBLE
                        binding.yesNo.visibility = View.VISIBLE
                        binding.itemWeNotSend.visibility = View.VISIBLE
                    } else {
                        binding.packageType.visibility = View.GONE
                        binding.yesNo.visibility = View.GONE
                        binding.itemWeNotSend.visibility = View.GONE
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToContact(it: List<MyLocationResAndEditLocationReq>) {
        val contactList: MutableList<MyLocationResAndEditLocationReq> = mutableListOf()
        for (myLocation in it) {
            contactList.add(myLocation)
            if (myLocation.DefaultDropoff == true) {
                try {
                    binding.dropName.setText(myLocation.Location?.ContactName.toString())
                    binding.dropEmail.setText(myLocation.Location?.Email.toString())
                    binding.dropPhone.setText(myLocation.Location?.Phone.toString())
                    if (!myLocation.Location?.Notes.isNullOrEmpty())
                        binding.dropInstruction.setText(myLocation.Location?.Notes.toString())
                    if (!myLocation.Location?.UnitNumber.isNullOrEmpty())
                        binding.dropUnit.setText(myLocation.Location?.UnitNumber.toString())
                    if (!myLocation.Location?.CompanyName.isNullOrEmpty())
                        binding.dropCompany.setText(myLocation.Location?.CompanyName.toString())
                    binding.dropAddress.text = myLocation.Location?.Street.toString()
                    dropState = myLocation.Location?.State.toString()
                    dropGpx = myLocation.Location?.GPSX.toString()
                    dropGpy = myLocation.Location?.GPSY.toString()
                    dropSuburb = myLocation.Location?.Suburb.toString()
                    dropPostCode = myLocation.Location?.Postcode.toString()
                    dropStreet = myLocation.Location?.Street.toString()
                    dropStreetNumber = myLocation.Location?.StreetNumber.toString()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        val dropAdapter = ContactNameAdapter(this, R.layout.autocomplete_text, contactList)
        binding.dropName.threshold = 0
        binding.dropName.setAdapter(dropAdapter)
        showHideWeight(pickUpDetails?.pickState, dropState)
        showErrorForSamePickAndDropAddress()
    }

    private fun showErrorForSamePickAndDropAddress() {
        isInterstate = pickUpDetails?.pickState != dropState

        if (!pickUpDetails?.pickGpx.isNullOrBlank() && !dropGpx.isNullOrBlank()) {
            if (pickUpDetails?.pickGpx == dropGpx && pickUpDetails?.pickGpy == dropGpy) {
                binding.dropAddressError.visibility = View.VISIBLE
                binding.dropAddressError.text = "*Please select different pick and drop address."
            } else
                binding.dropAddressError.visibility = View.GONE
        } else
            binding.dropAddressError.visibility = View.GONE
    }

    override fun onTouch(view: View?, p1: MotionEvent?): Boolean {
        when (view!!.id) {
            R.id.drop_address -> {
                dropSearchLocation()
            }
        }
        return true
    }

    private fun dropSearchLocation() {
        try {
            val fields =
                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setCountry("AU")
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivityForResult(intent, dropAutocompleteRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.no1->{
                isflexible=false
                binding.no1.setBackgroundResource(R.color.base_color)
                binding.yes1.setBackgroundResource(R.color.white)
            }
            R.id.yes1->{
                isflexible=true
                binding.no1.setBackgroundResource(R.color.white)
                binding.yes1.setBackgroundResource(R.color.base_color)
            }
            R.id.drop_find_me -> {
                getLocationClass?.getCurrentLocation(onAddress = ::getDropAddress)
            }
            R.id.drop_address -> {
                dropSearchLocation()
            }
            R.id.item_we_not_send -> {
                try {
                    val browserIntent: Intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://deliveries-staging.zoom2u.com/content/images/dg-check/dg-declaration.jpg")
                    )
                    startActivity(browserIntent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.authority_to -> {
                binding.spinner.performClick()

            }
            R.id.authority_to_leave -> {
                if (binding.authorityToLeave.isChecked)
                    binding.authorityTo.visibility = View.VISIBLE
                else
                    binding.authorityTo.visibility = View.GONE
            }
            R.id.no_contact_drop -> {
                if (binding.noContactDrop.isChecked) {
                    binding.authorityToLeave.isChecked = true
                    binding.authorityTo.visibility = View.VISIBLE
                }
            }
            R.id.back_btn -> {
                finish()
            }
            R.id.next_btn -> {
                /**check if pick or drop state is TAS*/
                if (dropState == "TAS") {
                    DialogActivity.alertDialogSingleButton(
                        this,
                        "Oops!",
                        "We're sorry,we currently don't service this route.Please contact us if you required more information! You can reach us on  1300 966 628."
                    )
                } else {
                    val isBookingConfirm = validateYourDeliveryField(
                        binding.dropName.text.toString().trim(),
                        binding.dropPhone.text.toString().trim(),
                        binding.dropAddress.text.toString().trim()
                    )
                    if (isBookingConfirm <= 0) {
                        if (pickUpDetails?.pickGpx == dropGpx && pickUpDetails?.pickGpy == dropGpy) {
                            binding.dropAddressError.visibility = View.VISIBLE
                            binding.dropAddressError.text =
                                "*Please select different pick and drop address."
                        }

                        /**for large item bid*/
                        else if(intent.hasExtra("LargeItem")){
                            /**for large item bid intrastate*/
                            if (pickUpDetails?.pickState == dropState) {
                                /**for large item bid General Van Shipments & Furniture*/
                                if(largeItem?.Value==15||largeItem?.Value==16){
                                    val intent = Intent(this, AvailabilityDeliveryActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.putExtra("isQuotesRequest",true)
                                    intent.putExtra("SaveDeliveryRequestReqForQuotes", createJsonForSaveRequest().toString())
                                    startActivityForResult(intent, 3)
                                }else{
                                    /**for large item bid heavy freight*/
                                    val intent = Intent(this, AvailabilityDeliveryHeavyFreightPickDateActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.putExtra("LargeItem", largeItem)
                                    intent.putExtra("SaveDeliveryRequestReqForQuotes", createJsonForSaveRequest().toString())
                                    startActivityForResult(intent, 3)
                                }

                            } else {
                                /**for large item bid heavy freight interstate*/
                                val intent = Intent(this, AvailabilityDeliveryHeavyFreightPickDateActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.putExtra("LargeItem", largeItem)
                                intent.putExtra("SaveDeliveryRequestReqForQuotes", createJsonForSaveRequest().toString())
                                startActivityForResult(intent, 3)
                            }

                        }
                       /**for normal item bid*/
                        else if (isQuotesRequest) {
                            /**for normal item bid intrastate*/
                            if (pickUpDetails?.pickState == dropState) {
                                val intent = Intent(this, AvailabilityDeliveryActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.putExtra("isQuotesRequest",isQuotesRequest)
                                intent.putExtra("SaveDeliveryRequestReqForQuotes", createJsonForSaveRequest().toString())
                                startActivityForResult(intent, 3)
                            } else {
                                /**for normal item bid interstate heavy freight*/
                                val intent = Intent(this, AvailabilityDeliveryHeavyFreightPickDateActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.putExtra("LargeItem", largeItem)
                                intent.putExtra("SaveDeliveryRequestReqForQuotes", createJsonForSaveRequest().toString())
                                startActivityForResult(intent, 3)
                            }
                        } else {
                            /**for normal booking*/
                            val intent = Intent(this, AvailabilityDeliveryActivity::class.java)
                            /**for Intra State**/
                            if (pickUpDetails?.pickState == dropState) {
                                isInterstate = false
                                intraStateReq = getIntraState()
                                intent.putExtra("IntraStateData", intraStateReq)

                            }
                            /**for Inter State**/
                            else {
                                isInterstate = true
                                interStateReq = getInterState()
                                intent.putExtra("InterStateData", interStateReq)
                            }
                            intent.putExtra("isQuotesRequest",isQuotesRequest)
                            intent.putExtra("SaveDeliveryRequestReq", createJsonForSaveRequest().toString())
                            intent.putExtra("SuggestPriceRequestReq", createJsonForSuggestPrice().toString())
                            intent.putParcelableArrayListExtra("IconList", itemDataList)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivityForResult(intent, 3)
                        }
                    } else
                        DialogActivity.alertDialogSingleButton(this, "Awaiting!", bookDeliveryAlertMsgStr)
                }
            }
        }
    }

    private fun getPickDateAndTimeInEta(): String {
        return AppUtility.getCurrentDateAndTimeInEta()
    }

    private fun getIntraState(): IntraStateReq? {

        val dropLocation = IntraStateReq.DropLocationClass(dropGpx, dropGpy)
        val pickLocation = IntraStateReq.PickupLocationClass(pickUpDetails?.pickGpx, pickUpDetails?.pickGpy)
        intraStateReq = IntraStateReq(
            AppUtility.getCurrentDateAndTimeInEta(),
            0,
            binding.dropAddress.text.toString(),
            dropLocation,
            dropPostCode,
            dropState,
            dropSuburb,
            false,
            false,
            pickUpDetails?.pickAddress,
            getPickDateAndTimeInEta(),
            pickLocation,
            pickUpDetails?.pickPostCode,
            pickUpDetails?.pickState,
            pickUpDetails?.pickSuburb,
            getShipmentsList()
        )

        return intraStateReq
    }

    private fun getInterState(): InterStateReq? {
        val dropLocation = InterStateReq.DropLocationClass(dropGpx, dropGpy)
        val pickLocation = InterStateReq.PickupLocationClass(pickUpDetails?.pickGpx, pickUpDetails?.pickGpy)
        interStateReq = InterStateReq(
            AppUtility.getCurrentDateAndTimeInEta(),
            binding.dropAddress.text.toString(),
            dropLocation,
            dropPostCode,
            dropState,
            dropStreet,
            dropStreetNumber,
            dropSuburb,
            false,
            false,
            pickUpDetails?.pickAddress,
            getPickDateAndTimeInEta(),
            pickLocation,
            pickUpDetails?.pickPostCode,
            pickUpDetails?.pickState,
            pickUpDetails?.pickStreet,
            pickUpDetails?.pickStreetNumber,
            pickUpDetails?.pickSuburb,
            IsLaptopOrMobileChecked,
            getShipmentsList(),
            countTotalWeight()
        )

        return interStateReq
    }

    private fun countTotalWeight(): String {
        var totalWeight = 0.0
        for (item in itemDataList) {
            totalWeight += item.weight * item.quantity
        }
        return totalWeight.toString()
    }

    private fun createJsonForSaveRequest(): JSONObject {

        val pickAdd = if (pickUpDetails?.pickUnit.isNullOrBlank())
            pickUpDetails?.pickStreet
        else
            pickUpDetails?.pickUnit?.trim() + "/" + pickUpDetails?.pickStreet.toString()


        val pickLocation: SaveDeliveryRequestReq.DeliveryRequestModel.PickupLocationClass =
            SaveDeliveryRequestReq.DeliveryRequestModel.PickupLocationClass(
                pickUpDetails?.pickName.toString(),
                pickUpDetails?.pickPhone.toString(),
                pickUpDetails?.pickEmail.toString(),
                pickAdd,
                pickUpDetails?.pickInstruction.toString(),
                pickUpDetails?.pickGpx, pickUpDetails?.pickGpy, pickUpDetails?.pickUnit.toString(),
                pickUpDetails?.pickStreetNumber, pickUpDetails?.pickStreet,
                pickUpDetails?.pickSuburb, pickUpDetails?.pickState,
                pickUpDetails?.pickPostCode, pickUpDetails?.pickPremisesType, false,
                pickUpDetails?.pickCompany.toString(), pickUpDetails?.pickCountry
            )


        val dropAdd = if (binding.dropUnit.text.toString().trim().isNullOrBlank())
            dropStreet.toString()
        else
            binding.dropUnit.text.toString().trim() + "/" + dropStreet.toString()


        val dropLocation: SaveDeliveryRequestReq.DeliveryRequestModel.DropLocationClass =
            SaveDeliveryRequestReq.DeliveryRequestModel.DropLocationClass(
                binding.dropName.text.toString(),
                binding.dropPhone.text.toString(),
                binding.dropEmail.text.toString(),
                dropAdd,
                binding.dropInstruction.text.toString(),
                dropGpx, dropGpy, binding.dropUnit.text.toString(),
                dropStreetNumber, dropStreet, dropSuburb,
                dropState, dropPostCode, dropPremisesType, false,
                binding.dropCompany.text.toString(), dropCountry
            )

        val jObjOfQuotesItem = JSONObject()
        val deliveryRequest = JSONObject()
        val forInterstate = JSONObject()
        val authorityToLeaveForm = JSONObject()
        try {


            deliveryRequest.put("PickupLocationPremisesType",  "y")
            deliveryRequest.put("DropLocationPremisesType",  "y")
            deliveryRequest.put("IsInterstate", isInterstate)
            deliveryRequest.put("LoadType", "FullLoad")
            deliveryRequest.put("DropIdentityType", "DriversLicence")
            deliveryRequest.put("Status",  "Active")
            deliveryRequest.put("isPickupAddressManuallyEntered", false)
            deliveryRequest.put("Source", 9)
            deliveryRequest.put("DropLocation", JSONObject(Gson().toJson(dropLocation).toString()))
            deliveryRequest.put("PickupLocation", JSONObject(Gson().toJson(pickLocation).toString()))
            deliveryRequest.put("sendSmsToPickupPerson", pickUpDetails?.sendSmsToPickupPerson)
            deliveryRequest.put("IsNoContactPickup", pickUpDetails?.isNoContactPickup)
            deliveryRequest.put("IsNoContactDrop", binding.noContactDrop.isChecked)
            deliveryRequest.put("isLaptopOrMobile", isLaptopOrMobile)
            deliveryRequest.put("AuthorityToLeave", binding.authorityToLeave.isChecked)
            deliveryRequest.put("Instructions", binding.other.text.toString())
            deliveryRequest.put("isCreatedFromQuotes", false)
            deliveryRequest.put("DeclarationSignature", "")
            deliveryRequest.put("PickupLocationTailLiftNotes", pickUpDetails?.PickupLocationTailLiftNotes)
            if(binding.facilitiesPick.isVisible)
                deliveryRequest.put("PickupLocationTailLiftType", facilitiesPick)
            else
                deliveryRequest.put("PickupLocationTailLiftType", pickUpDetails?.PickupLocationTailLiftType)
            deliveryRequest.put("DropLocationTailLiftType",facilities)
            deliveryRequest.put("DropLocationTailLiftNotes", binding.freightNote.text.toString())
            deliveryRequest.put("PricingPlanChangeHistoryId", 1)
            deliveryRequest.put("PricingScheme", AppUtility.getAccountType())


            if (binding.authorityToLeave.isChecked) {
                deliveryRequest.put("LeaveAt", leaveAt.toString())
                deliveryRequest.put("AuthorityToLeave", true)
                deliveryRequest.put("LeaveAt", leaveAt.toString())
                authorityToLeaveForm.put("NoContact", binding.noContactDrop.isChecked)
                authorityToLeaveForm.put("LeaveAt", leaveAt.toString())
                authorityToLeaveForm.put("Instructions", binding.other.text.toString())
                jObjOfQuotesItem.put("_authorityToLeaveForm", authorityToLeaveForm)
            } else {
                jObjOfQuotesItem.put("_authorityToLeaveForm", null)
                deliveryRequest.put("LeaveAt", "")
                deliveryRequest.put("AuthorityToLeave", false)
            }


            /**bid for large item*/
            if(intent.hasExtra("LargeItem")){

                when (largeItem?.Value) {
                    15 -> deliveryRequest.put("FreightCategory", 0)
                    16 -> deliveryRequest.put("FreightCategory", 1)
                    18 -> deliveryRequest.put("FreightCategory", 2)
                    19 -> deliveryRequest.put("FreightCategory", 3)
                    20->  deliveryRequest.put("FreightCategory", 4)
                    21->  deliveryRequest.put("FreightCategory", 5)
                    22->  deliveryRequest.put("FreightCategory", 6)
                    23->  deliveryRequest.put("FreightCategory", 7)
                    24->  deliveryRequest.put("FreightCategory", 8)
                }
                deliveryRequest.put("Weight", "")
                jObjOfQuotesItem.put("_requestModel", deliveryRequest)
                if(largeItem?.Value==15||largeItem?.Value==16) {
                    jObjOfQuotesItem.put("_shipmentModel", JSONArray(Gson().toJson(getLargeShipmentsList()).toString()))

                    if(isInterstate != true) {
                        deliveryRequest.put("isDropAddressManuallyEntered", false)
                        deliveryRequest.put("RequestedDropDateTimeWindowEnd", AppUtility.getDropOffDateAndTimeEnd())
                        deliveryRequest.put("RequestedDropDateTimeWindowStart", AppUtility.getDropOffDateAndTimeStart())
                        deliveryRequest.put("RequestedPickupDateTimeWindowEnd", AppUtility.getPickupDateAndTimeEnd())
                        deliveryRequest.put("RequestedPickupDateTimeWindowStart", AppUtility.getCurrentDateAndTimeInEta())
                    }

                }

            }
            else {
                deliveryRequest.put("Weight", countTotalWeight())
                deliveryRequest.put("isDropAddressManuallyEntered", false)


                if(itemDataList.size!=0){
                    val selectedIcon=itemDataList[0]
                    when (selectedIcon.Value) {
                        10 -> deliveryRequest.put("FreightCategory", 10)
                        11 -> deliveryRequest.put("FreightCategory", 11)
                        12 -> deliveryRequest.put("FreightCategory", 12)
                        13 -> deliveryRequest.put("FreightCategory", 13)
                        14 -> deliveryRequest.put("FreightCategory", 14)
                    }
                }


                /**bid for normal item*/
                if (isQuotesRequest) {
                    if(isInterstate == true){
                       deliveryRequest.put("FreightCategory", 10)
                   }else{
                        deliveryRequest.put("RequestedDropDateTimeWindowEnd", AppUtility.getDropOffDateAndTimeEnd())
                        deliveryRequest.put("RequestedDropDateTimeWindowStart", AppUtility.getDropOffDateAndTimeStart())
                        deliveryRequest.put("RequestedPickupDateTimeWindowEnd", AppUtility.getPickupDateAndTimeEnd())
                        deliveryRequest.put("RequestedPickupDateTimeWindowStart", AppUtility.getCurrentDateAndTimeInEta())
                        deliveryRequest.put("FreightCategory", 14)
                        deliveryRequest.put("Vehicle", "")
                        jObjOfQuotesItem.put("_shipmentModel", JSONArray(Gson().toJson(getShipmentsList()).toString()))
                   }
                    jObjOfQuotesItem.put("_requestModel", deliveryRequest)

                }

                else {
                    /**for normal item booking*/
                    jObjOfQuotesItem.put("_shipmentModel", JSONArray(Gson().toJson(getShipmentsList()).toString()))
                    jObjOfQuotesItem.put("_deliveryRequestModel", deliveryRequest)
                    jObjOfQuotesItem.put("_interstateModel", forInterstate)
                    deliveryRequest.put("RequestedDropDateTimeWindowEnd", AppUtility.getDropOffDateAndTimeEnd())
                    deliveryRequest.put("RequestedDropDateTimeWindowStart", AppUtility.getDropOffDateAndTimeStart())
                    deliveryRequest.put("RequestedPickupDateTimeWindowEnd", AppUtility.getPickupDateAndTimeEnd())
                    deliveryRequest.put("RequestedPickupDateTimeWindowStart", AppUtility.getCurrentDateAndTimeInEta())
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jObjOfQuotesItem
    }

    private fun createJsonForSuggestPrice(): JSONObject {

        val pickAdd = if (pickUpDetails?.pickUnit.isNullOrBlank())
            pickUpDetails?.pickStreet
        else
            pickUpDetails?.pickUnit?.trim() + "/" + pickUpDetails?.pickStreet.toString()


        val pickLocation: SaveDeliveryRequestReq.DeliveryRequestModel.PickupLocationClass =
            SaveDeliveryRequestReq.DeliveryRequestModel.PickupLocationClass(
                pickUpDetails?.pickName.toString(),
                pickUpDetails?.pickPhone.toString(),
                pickUpDetails?.pickEmail.toString(),
                pickAdd,
                pickUpDetails?.pickInstruction.toString(),
                pickUpDetails?.pickGpx, pickUpDetails?.pickGpy, pickUpDetails?.pickUnit.toString(),
                pickUpDetails?.pickStreetNumber, pickUpDetails?.pickStreet,
                pickUpDetails?.pickSuburb, pickUpDetails?.pickState,
                pickUpDetails?.pickPostCode, pickUpDetails?.pickPremisesType, false,
                pickUpDetails?.pickCompany.toString(), pickUpDetails?.pickCountry
            )


        val dropAdd = if (binding.dropUnit.text.toString().trim().isNullOrBlank())
            dropStreet.toString()
        else
            binding.dropUnit.text.toString().trim() + "/" + dropStreet.toString()


        val dropLocation: SaveDeliveryRequestReq.DeliveryRequestModel.DropLocationClass =
            SaveDeliveryRequestReq.DeliveryRequestModel.DropLocationClass(
                binding.dropName.text.toString(),
                binding.dropPhone.text.toString(),
                binding.dropEmail.text.toString(),
                dropAdd,
                binding.dropInstruction.text.toString(),
                dropGpx, dropGpy, binding.dropUnit.text.toString(),
                dropStreetNumber, dropStreet, dropSuburb,
                dropState, dropPostCode, dropPremisesType, false,
                binding.dropCompany.text.toString(), dropCountry
            )

        val jObjOfQuotesItem = JSONObject()
        val deliveryRequest = JSONObject()
        try {


            deliveryRequest.put("PickupLocationPremisesType",  "y")
            deliveryRequest.put("DropLocationPremisesType",  "y")
            deliveryRequest.put("IsInterstate", isInterstate)
            deliveryRequest.put("LoadType", "FullLoad")
            deliveryRequest.put("DropIdentityType", "DriversLicence")
            deliveryRequest.put("Status",  "Active")
            deliveryRequest.put("isPickupAddressManuallyEntered", false)
            deliveryRequest.put("isDropAddressManuallyEntered", false)
            deliveryRequest.put("Source", 9)
            deliveryRequest.put("DropLocation", JSONObject(Gson().toJson(dropLocation).toString()))
            deliveryRequest.put("PickupLocation", JSONObject(Gson().toJson(pickLocation).toString()))
            deliveryRequest.put("sendSmsToPickupPerson", pickUpDetails?.sendSmsToPickupPerson)
            deliveryRequest.put("IsNoContactPickup", pickUpDetails?.isNoContactPickup)
            deliveryRequest.put("IsNoContactDrop", binding.noContactDrop.isChecked)
            deliveryRequest.put("RequestedDropDateTimeWindowEnd", AppUtility.getDropOffDateAndTimeEnd())
            deliveryRequest.put("RequestedDropDateTimeWindowStart", AppUtility.getDropOffDateAndTimeStart())
            deliveryRequest.put("RequestedPickupDateTimeWindowEnd", AppUtility.getPickupDateAndTimeEnd())
            deliveryRequest.put("RequestedPickupDateTimeWindowStart", AppUtility.getCurrentDateAndTimeInEta())
            deliveryRequest.put("isLaptopOrMobile", isLaptopOrMobile)
            deliveryRequest.put("AuthorityToLeave", binding.authorityToLeave.isChecked)
            deliveryRequest.put("Instructions", binding.other.text.toString())
            deliveryRequest.put("isCreatedFromQuotes", false)
            deliveryRequest.put("DeclarationSignature", "")
            deliveryRequest.put("PickupLocationTailLiftNotes", pickUpDetails?.PickupLocationTailLiftNotes)
            deliveryRequest.put("PickupLocationTailLiftType", pickUpDetails?.PickupLocationTailLiftType)
            deliveryRequest.put("DropLocationTailLiftType",facilities)
            deliveryRequest.put("DropLocationTailLiftNotes", binding.freightNote.text.toString())
            deliveryRequest.put("PricingPlanChangeHistoryId", 1)
            deliveryRequest.put("PricingScheme", AppUtility.getAccountType())
            deliveryRequest.put("FreightCategory", 10)
            deliveryRequest.put("Weight", countTotalWeight())
            deliveryRequest.put("FreightTitle", "")
            deliveryRequest.put("FreightValue", "")
            deliveryRequest.put("OtherDetails", "")
            deliveryRequest.put("VehicleBrand","")
            deliveryRequest.put("VehicleModel", "")
            deliveryRequest.put("isDrivable",false)
            deliveryRequest.put("IsVehicleWithBelongings",false)
            deliveryRequest.put("IsSuggestedPrice",true)
            deliveryRequest.put("IsRentContainer",false)
            deliveryRequest.put("IsEmptyVehicle", false)
            deliveryRequest.put("SecurityIdCardNumber", "")
            deliveryRequest.put("OrderNumber", "")
            deliveryRequest.put("BookingFee", 0)
            deliveryRequest.put("DeliverySpeed", "")
            deliveryRequest.put("Distance",  "7.2 km")
            deliveryRequest.put("DropIdentityNumber",  "")
            deliveryRequest.put("IsDropIdCheckRequired",  false)
            deliveryRequest.put("LoadType",  "FullLoad")
            deliveryRequest.put("PackagingNotes",  "")
            deliveryRequest.put("TrailerType",  "")
            deliveryRequest.put("isPayByInvoiceMarked",  false)



            if (binding.authorityToLeave.isChecked) {
                deliveryRequest.put("AuthorityToLeave", true)
                deliveryRequest.put("LeaveAt", leaveAt.toString())
                deliveryRequest.put("Instructions", binding.other.text.toString())

            } else {
                deliveryRequest.put("LeaveAt", "")
                deliveryRequest.put("AuthorityToLeave", false)
            }

            if(isInterstate==true){
                deliveryRequest.put("Items", JSONArray(Gson().toJson(getItemList()).toString()))
                deliveryRequest.put("Vehicle", "Car")
                jObjOfQuotesItem.put("_requestModel", deliveryRequest)
            }else{
                deliveryRequest.put("Items", JSONArray(Gson().toJson(getEmptyItemList()).toString()))
                deliveryRequest.put("Vehicle", "Bike")
                jObjOfQuotesItem.put("_requestModel", deliveryRequest)
                jObjOfQuotesItem.put("_shipmentModel", JSONArray(Gson().toJson(getShipmentsList()).toString()))
            }


                }catch (e: JSONException) {
            e.printStackTrace()
        }
        return jObjOfQuotesItem
    }

    private fun getEmptyItemList(): MutableList<ItemHeavyFreight> {
        val itemList: MutableList<ItemHeavyFreight> = ArrayList()
        val item = ItemHeavyFreight(
                "", "", "", "",
                "","","","")
        itemList.add(0, item)

        return itemList
    }

    private fun getItemList(): MutableList<ItemHeavyFreight> {
        var item: ItemHeavyFreight
        val itemList: MutableList<ItemHeavyFreight> = ArrayList()
        for ((i, item1) in itemDataList.withIndex()) {

            item = ItemHeavyFreight(
                item1.Category,
                item1.quantity.toString(), item1.length.toString(), item1.height.toString(),
                item1.width.toString(), item1.ItemWeightKg.toString(),(item1.weight*item1.quantity).toString(),""
            )
            itemList.add(i, item)
        }
        return itemList
    }

    private fun getLargeShipmentsList(): MutableList<LargeShipmentsClass> {
        val shipmentsList: MutableList<LargeShipmentsClass> = ArrayList()
        val shipments = LargeShipmentsClass(largeItem?.Category, 1, 15)
        shipmentsList.add(0, shipments)
        return shipmentsList
    }

    private fun getShipmentsList(): MutableList<ShipmentsClass> {
        var shipments: ShipmentsClass
        val shipmentsList: MutableList<ShipmentsClass> = ArrayList()
        for ((i, item) in itemDataList.withIndex()) {

            shipments = ShipmentsClass(
                item.Category,
                item.quantity,item.Value, item.length, item.height,
                item.width, item.weight, (item.weight*item.quantity).toString()
            )
            shipmentsList.add(i, shipments)
        }
        return shipmentsList
    }

    private fun getDropAddress(lat: Double, lang: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lang, 1)
        binding.dropAddress.text = address[0].getAddressLine(0)
        viewModel.addFromGoogleLatLang(lat.toString(), lang.toString(), false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == dropAutocompleteRequest) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    binding.dropAddress.text = place.address
                    viewModel.addFromGoogleAdd(place.address, false)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    binding.dropAddress.text = ""
                }
                RESULT_CANCELED -> {
                    Log.i("Place API Failure", "  -------------- User Cancelled -------------")
                }
            }
        }

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        val selectedText = categories[position]
        binding.authorityTo.setText(selectedText)
        leaveAt = position
        if (selectedText == "Other") {
            binding.other.visibility = View.VISIBLE
        } else {
            binding.other.visibility = View.GONE

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

    private fun validateYourDeliveryField(
        dropName: String,
        dropPhone: String,
        dropAddress: String
    ): Int {
        var bookDeliveryAlertCount = 0
        bookDeliveryAlertMsgStr = ""




        /******************* Validation for drop off detail ******************/
        if (dropName == ""
            || dropPhone == ""
            || dropAddress == ""
            || !dropPhone.matches(("^[\\s0-9\\()\\-\\+]+$").toRegex())
        ) {
            if (dropName == "") {
                AppUtility.validateEditTextField(binding.dropName, "Please enter contact name.")
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at dropoff name.", bookDeliveryAlertCount)
            }
            if (dropPhone == "") {
                AppUtility.validateEditTextField(binding.dropPhone, "Please enter mobile number.")
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at dropoff's number.", bookDeliveryAlertCount)
            } else if (!dropPhone.matches(("^[\\s0-9\\()\\-\\+]+$").toRegex())) {

                AppUtility.validateEditTextField(
                    binding.dropPhone,
                    "Please enter valid mobile number."
                )
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at dropoff's number.", bookDeliveryAlertCount)
            }
            if (dropAddress == "") {
                AppUtility.validateTextField(binding.dropAddress, "Please enter address.")
                bookDeliveryAlertCount++
                addTextToAlertDialog("Contact at dropoff's address", bookDeliveryAlertCount)
            }
        }

        /**for drop authority to leave*/
        if (binding.authorityToLeave.isChecked) {
            if (binding.authorityTo.text.toString() == "Other") {
                if (binding.other.text.toString() == "") {
                    AppUtility.validateEditTextField(
                        binding.other,
                        "Please enter a safe place on where to leave your parcel."
                    )
                    bookDeliveryAlertCount++
                    addTextToAlertDialog(
                        "Please enter a safe place on where to leave your parcel.",
                        bookDeliveryAlertCount
                    )
                }
            }
        }


        return bookDeliveryAlertCount
    }


    private fun addTextToAlertDialog(addAlertMsgStr: String, count: Int) {
        bookDeliveryAlertMsgStr =
            if (bookDeliveryAlertMsgStr == "") "$bookDeliveryAlertMsgStr$count) $addAlertMsgStr" else "$bookDeliveryAlertMsgStr\n$count) $addAlertMsgStr"

    }
}