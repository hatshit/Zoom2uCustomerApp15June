package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.update_details_heavy

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
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
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.apiclient.GetAddressFromGoogleAPI
import com.zoom2u_customer.apiclient.GoogleServiceApi
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityUpdateDetailsHeavyBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.payment_heavy.PaymentHeavyActivity
import com.zoom2u_customer.ui.application.bottom_navigation.history.HistoryRepository
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.DeliveryDetailsViewModel
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.SaveDeliveryRequestReq
import com.zoom2u_customer.ui.application.get_location.GetLocationClass
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class UpdateDetailsHeavyActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {
    var bookDeliveryAlertMsgStr = ""
    private var pickAutocompleteRequest = 1019
    private var dropAutocompleteRequest = 1018
    private var heavyFreightBidDetails: HeavyFreightBidDetails? = null
    lateinit var binding: ActivityUpdateDetailsHeavyBinding
    private var offer: Offer? = null
    private var getLocationClass: GetLocationClass? = null
    lateinit var viewModel: DeliveryDetailsViewModel
    lateinit var updateDetailsViewModel: UpdateDetailsHeavyViewModel
    private var updateDetailsHeavyRepository: UpdateDetailsHeavyRepository? = null
    private var repositoryGoogleAddress: GoogleAddressRepository? = null

    private var pickState: String? = null
    private var pickStreetNumber: String? = null
    private var pickStreet: String? = null
    private var pickGpx: String? = null
    private var pickGpy: String? = null
    private var pickSuburb: String? = null
    private var pickPostCode: String? = null
    private var pickCountry: String? = null

    private var dropState: String? = null
    private var dropStreetNumber: String? = null
    private var dropStreet: String? = null
    private var dropGpx: String? = null
    private var dropGpy: String? = null
    private var dropSuburb: String? = null
    private var dropPostCode: String? = null
    private var dropCountry: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_details_heavy)
        if (intent.hasExtra("HeavyFreightBidDetails")) {
            heavyFreightBidDetails = intent.getParcelableExtra("HeavyFreightBidDetails")
            offer = intent.getParcelableExtra("offer")
            setDataToView()
        }
        binding.pickAddress.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.pickFindMe.setOnClickListener(this)
        binding.dropFindMe.setOnClickListener(this)
        binding.dropAddress.setOnClickListener(this)
        binding.nextBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
        viewModel = ViewModelProvider(this)[DeliveryDetailsViewModel::class.java]
        updateDetailsViewModel = ViewModelProvider(this)[UpdateDetailsHeavyViewModel::class.java]
        val serviceApi: ServiceApi = getServices()
        val googleServiceApi: GoogleServiceApi = GetAddressFromGoogleAPI.getGoogleServices()
        updateDetailsHeavyRepository = UpdateDetailsHeavyRepository(serviceApi, this)
        repositoryGoogleAddress = GoogleAddressRepository(googleServiceApi, this)
        viewModel.repositoryGoogleAddress = repositoryGoogleAddress
        updateDetailsViewModel.repository = updateDetailsHeavyRepository
        getLocationClass = GetLocationClass(this)

        if (!Places.isInitialized()) {
            val apiKey = getString(R.string.google_api_ke)
            Places.initialize(applicationContext, apiKey)
        }

        updateDetailsViewModel.getUpdateDetailsSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                val intent = Intent(this,PaymentHeavyActivity::class.java)
                intent.putExtra("PaymentHeavyActivity", it)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        }

        viewModel.googleSuccessUsingAdd()?.observe(this) { isGoogleAdd ->
            if (isGoogleAdd.isNotEmpty()) {
                AppUtility.progressBarDissMiss()
                val getAddress: HashMap<String, Any>? = isGoogleAdd
                if (isGoogleAdd["isTrue"] == "true") {

                    pickGpx =
                        getAddress?.get("latitude").toString()

                    pickGpy =
                        getAddress?.get("longitude").toString()

                    pickState = getAddress?.get("state")?.toString()

                    pickSuburb =
                        getAddress?.get("suburb")?.toString()

                    pickPostCode =
                        getAddress?.get("postcode")?.toString()

                    pickStreet =
                        getAddress?.get("address")?.toString()

                    pickStreetNumber =
                        getAddress?.get("streetNumber")?.toString()

                    pickCountry =
                        getAddress?.get("country")?.toString()


                } else {

                    dropGpx =
                        getAddress?.get("latitude").toString()

                    dropGpy =
                        getAddress?.get("longitude").toString()

                    dropState = getAddress?.get("state")?.toString()


                    dropSuburb =
                        getAddress?.get("suburb")?.toString()

                    dropPostCode =
                        getAddress?.get("postcode")?.toString()

                    dropStreet =
                        getAddress?.get("address")?.toString()

                    dropStreetNumber =
                        getAddress?.get("streetNumber")?.toString()

                    dropCountry =
                        getAddress?.get("country")?.toString()

                }

            }
        }

        viewModel.googleSuccessUsingLatLang()?.observe(this) { isGoogleAdd ->
            if (isGoogleAdd.isNotEmpty()) {
                AppUtility.progressBarDissMiss()
                val getAddress: HashMap<String, Any>? = isGoogleAdd
                if (isGoogleAdd["isTrue"] == "true") {

                    pickGpx =
                        getAddress?.get("latitude").toString()

                    pickGpy =
                        getAddress?.get("longitude").toString()

                    pickState = getAddress?.get("state")?.toString()


                    pickSuburb =
                        getAddress?.get("suburb")?.toString()

                    pickPostCode =
                        getAddress?.get("postcode")?.toString()

                    pickStreet =
                        getAddress?.get("address")?.toString()

                    pickStreetNumber =
                        getAddress?.get("streetNumber")?.toString()

                    pickCountry =
                        getAddress?.get("country")?.toString()

                } else {
                    dropGpx =
                        getAddress?.get("latitude").toString()

                    dropGpy =
                        getAddress?.get("longitude").toString()

                    dropState = getAddress?.get("state")?.toString()


                    dropSuburb =
                        getAddress?.get("suburb")?.toString()

                    dropPostCode =
                        getAddress?.get("postcode")?.toString()

                    dropStreet =
                        getAddress?.get("address")?.toString()

                    dropStreetNumber =
                        getAddress?.get("streetNumber")?.toString()

                    dropCountry =
                        getAddress?.get("country")?.toString()
                }

            }
        }

    }

    private fun setDataToView() {
        binding.pickName.setText(heavyFreightBidDetails?.PickupLocation?.ContactName)
        binding.pickEmail.setText(heavyFreightBidDetails?.PickupLocation?.Email)
        binding.pickPhone.setText(heavyFreightBidDetails?.PickupLocation?.Phone)
        binding.pickCompany.setText(heavyFreightBidDetails?.PickupLocation?.CompanyName)
        binding.pickUnit.setText(heavyFreightBidDetails?.PickupLocation?.UnitNumber)
        binding.pickAddress.setText(heavyFreightBidDetails?.PickupLocation?.Address)
        pickSuburb=heavyFreightBidDetails?.PickupLocation?.Suburb
        pickGpx=heavyFreightBidDetails?.PickupLocation?.GPSX
        pickGpy=heavyFreightBidDetails?.PickupLocation?.GPSY
        pickStreet=heavyFreightBidDetails?.PickupLocation?.Street
        pickState=heavyFreightBidDetails?.PickupLocation?.State
        pickStreetNumber=heavyFreightBidDetails?.PickupLocation?.StreetNumber
        pickPostCode=heavyFreightBidDetails?.PickupLocation?.Postcode




        binding.dropName.setText(heavyFreightBidDetails?.DropLocation?.ContactName)
        binding.dropEmail.setText(heavyFreightBidDetails?.DropLocation?.Email)
        binding.dropPhone.setText(heavyFreightBidDetails?.DropLocation?.Phone)
        binding.dropCompany.setText(heavyFreightBidDetails?.DropLocation?.CompanyName)
        binding.dropUnit.setText(heavyFreightBidDetails?.DropLocation?.UnitNumber)
        binding.dropAddress.setText(heavyFreightBidDetails?.DropLocation?.Address)
        dropSuburb=heavyFreightBidDetails?.PickupLocation?.Suburb
        dropGpx=heavyFreightBidDetails?.DropLocation?.GPSX
        dropGpy=heavyFreightBidDetails?.DropLocation?.GPSY
        dropStreet=heavyFreightBidDetails?.DropLocation?.Address
        dropState=heavyFreightBidDetails?.DropLocation?.State
        dropStreetNumber=heavyFreightBidDetails?.DropLocation?.StreetNumber
        dropPostCode=heavyFreightBidDetails?.DropLocation?.Postcode

    }

    override fun onTouch(view: View?, p1: MotionEvent?): Boolean {
        when (view?.id) {
            R.id.pick_address -> {
                pickSearchLocation()
            }
            R.id.drop_address -> {
                dropSearchLocation()
            }
        }
        return true
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.cancel_btn -> {
                finish()
            }
            R.id.back_btn -> {
                finish()
            }
            R.id.pick_find_me -> {
                getLocationClass?.getCurrentLocation(onAddress = ::getPickAddress)
            }
            R.id.pick_address -> {
                pickSearchLocation()
            }
            R.id.drop_find_me -> {
                getLocationClass?.getCurrentLocation(onAddress = ::getDropAddress)
            }
            R.id.drop_address -> {
                dropSearchLocation()
            }
            R.id.next_btn->{
                val isBookingConfirm = validateYourDeliveryField(
                    binding.pickName.text.toString().trim(),
                    binding.pickPhone.text.toString().trim(),
                    binding.pickAddress.text.toString().trim(),
                    binding.dropName.text.toString().trim(),
                    binding.dropPhone.text.toString().trim(),
                    binding.dropAddress.text.toString().trim())
                if (isBookingConfirm <= 0) {
                    val updateDetailsReq=UpdateDetailsReq(heavyFreightBidDetails?.QuoteRequestId,offer?.OfferId,
                        offer?.CarrierPrice,offer?.Price,binding.purNo.text.toString())

                    updateDetailsViewModel.updateDetailsHeavy(createJsonForSaveRequest(),updateDetailsReq)
                }else
                    DialogActivity.alertDialogSingleButton(this, "Awaiting!", bookDeliveryAlertMsgStr)
            }
        }
    }

    private fun createJsonForSaveRequest(): JSONObject {

        val pickAdd = if (binding.pickUnit.text.toString().trim().isNullOrBlank())
            pickStreet.toString()
        else
            binding.pickUnit.text.toString().trim() + "/" + pickStreet.toString()


        val dropAdd = if (binding.dropUnit.text.toString().trim().isNullOrBlank())
            dropStreet.toString()
        else
            binding.dropUnit.text.toString().trim() + "/" + dropStreet.toString()


        val deliveryRequest = JSONObject()
        val dropLocationRequest = JSONObject()
        val pickLocationRequest = JSONObject()

        try {

            pickLocationRequest.put("Address",pickAdd)
            pickLocationRequest.put("CompanyName",binding.pickCompany.text.toString())
            pickLocationRequest.put("ContactName",binding.pickName.text.toString())
            pickLocationRequest.put("Email",binding.pickEmail.text.toString())
            pickLocationRequest.put("GPSX",pickGpx)
            pickLocationRequest.put("GPSY",pickGpy)
            pickLocationRequest.put("GpsCoordinates","")
            pickLocationRequest.put("IsFlexible",heavyFreightBidDetails?.PickupLocation?.IsFlexible)
            pickLocationRequest.put("Notes", heavyFreightBidDetails?.PickupLocation?.Notes)
            pickLocationRequest.put("Phone",binding.pickPhone.text.toString())
            pickLocationRequest.put("Postcode",pickPostCode)
            pickLocationRequest.put("State",pickState)
            pickLocationRequest.put("Street",pickStreet)
            pickLocationRequest.put("StreetNumber",pickStreetNumber)
            pickLocationRequest.put("Suburb",pickSuburb)
            pickLocationRequest.put("UnitNumber",binding.pickUnit.text.toString())

            dropLocationRequest.put("Address",dropAdd)
            dropLocationRequest.put("CompanyName",binding.dropCompany.text.toString())
            dropLocationRequest.put("ContactName",binding.dropName.text.toString())
            dropLocationRequest.put("Email",binding.dropEmail.text.toString())
            dropLocationRequest.put("GPSX",dropGpx)
            dropLocationRequest.put("GPSY",dropGpy)
            dropLocationRequest.put("GpsCoordinates","")
            dropLocationRequest.put("IsFlexible", heavyFreightBidDetails?.DropLocation?.IsFlexible)
            dropLocationRequest.put("Notes", heavyFreightBidDetails?.DropLocation?.Notes)
            dropLocationRequest.put("Phone",binding.dropPhone.text.toString())
            dropLocationRequest.put("Postcode",dropPostCode)
            dropLocationRequest.put("State",dropState)
            dropLocationRequest.put("Street",dropState)
            dropLocationRequest.put("StreetNumber",dropStreetNumber)
            dropLocationRequest.put("Suburb",dropSuburb)
            dropLocationRequest.put("UnitNumber",binding.dropUnit.text.toString())

            deliveryRequest.put("_dropLocation", dropLocationRequest)
            deliveryRequest.put("_pickupLocation", pickLocationRequest)
            deliveryRequest.put("_quoteRequestId", heavyFreightBidDetails?.QuoteRequestId)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return deliveryRequest
    }

    private fun getPickAddress(lat: Double, lang: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lang, 1)
        binding.pickAddress.setText(address[0].getAddressLine(0))
        viewModel.addFromGoogleLatLang(lat.toString(), lang.toString(), true)
    }

    private fun getDropAddress(lat: Double, lang: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lang, 1)
        binding.dropAddress.setText(address[0].getAddressLine(0))
        viewModel.addFromGoogleLatLang(lat.toString(), lang.toString(), false)
    }

    private fun pickSearchLocation() {
        try {
            val fields = listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickAutocompleteRequest) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    binding.pickAddress.setText(place.address)
                    viewModel.addFromGoogleAdd(place.address, true)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    binding.pickAddress.setText("")
                }
                RESULT_CANCELED -> {
                    Log.i("Place API Failure", "  -------------- User Cancelled -------------")
                }
            }
        } else if (requestCode == dropAutocompleteRequest) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    binding.dropAddress.setText(place.address)
                    viewModel.addFromGoogleAdd(place.address, false)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    binding.dropAddress.setText("")
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
        pickAddress: String,
        dropName: String,
        dropPhone: String,
        dropAddress: String
    ) : Int{
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
                    binding.pickPhone,
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
        return bookDeliveryAlertCount
    }

    private fun addTextToAlertDialog(addAlertMsgStr: String, count: Int) {
        if (bookDeliveryAlertMsgStr == "") bookDeliveryAlertMsgStr =
            "$bookDeliveryAlertMsgStr$count) $addAlertMsgStr" else bookDeliveryAlertMsgStr =
            "$bookDeliveryAlertMsgStr\n$count) $addAlertMsgStr"

    }
}
