package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.edit_add_location

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.apiclient.GetAddressFromGoogleAPI.Companion.getGoogleServices
import com.zoom2u_customer.apiclient.GoogleServiceApi
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityEditLocationBinding
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.AddLocationReq
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq
import com.zoom2u_customer.ui.application.get_location.GetLocationClass
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import java.util.*

class EditAddLocationActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var placeAutocompleteRequest = 1019
    lateinit var binding: ActivityEditLocationBinding
    private var isEdit: Boolean = false
    private var myLocationResponse: MyLocationResAndEditLocationReq? = null
    private var addLocationReq: AddLocationReq? = null
    lateinit var viewModel: EditAddLocationViewModel
    private var repository: EditAddLocationRepository? = null
    private var repositoryGoogleAddress: GoogleAddressRepository? = null
    private var getLocationClass: GetLocationClass? = null
    private var lattitude: Double? = null
    private var longitude: Double? = null
    private var country: String? = null
    private var state: String? = null
    private var street: String? = null
    private var streetNumber: String? = null
    private var suburb: String? = null
    private var postCode: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_location)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl,this)
        AppUtility.hideKeyBoardClickOutside(binding.ll1,this)
        AppUtility.hideKeyboardActivityLunched(this)
        getLocationClass = GetLocationClass(this)

        if (intent.hasExtra("EditAddLocation")) {
            isEdit = intent.getBooleanExtra("EditAddLocation", false)
            if (isEdit) {
                binding.header.text = "Edit Location"
                binding.removeCl.visibility = View.VISIBLE
                myLocationResponse = intent.getParcelableExtra("EditLocation")
                showEditDataView(myLocationResponse)
            } else {
                binding.removeCl.visibility = View.GONE
                binding.header.text = "Add Location"

            }
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.findMe.setOnClickListener(this)

        binding.saveChangeBtn.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.removeTxt.setOnClickListener(this)
        binding.address.setOnClickListener(this)

        viewModel = ViewModelProvider(this).get(EditAddLocationViewModel::class.java)
        val serviceApi: ServiceApi = getServices()
        val googleServiceApi: GoogleServiceApi = getGoogleServices()
        repository = EditAddLocationRepository(serviceApi, this)
        viewModel.repository = repository

        repositoryGoogleAddress = GoogleAddressRepository(googleServiceApi, this)
        viewModel.repositoryGoogleAdd = repositoryGoogleAddress

        viewModel.getEditLocationSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                if (it != "") {
                    val intent = Intent()
                    setResult(3, intent)
                    Toast.makeText(
                        this,
                        "My Location details updated successfully.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }
            viewModel.getAddLocationSuccess()?.observe(this) {
                if (!TextUtils.isEmpty(it)) {
                    AppUtility.progressBarDissMiss()
                    if (it != "") {
                        val intent = Intent()
                        setResult(4, intent)
                        Toast.makeText(
                            this,
                            "My Location details Added successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }

            }

            viewModel.getDeleteLocationSuccess()?.observe(this) {
                if (!TextUtils.isEmpty(it)) {
                    AppUtility.progressBarDissMiss()
                    if (it != "") {
                        val intent = Intent()
                        setResult(3, intent)
                        Toast.makeText(
                            this,
                            "Location delete successfully.",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }

            }

            /**find add when search from place*/
            viewModel.googleSuccessUsingAdd()?.observe(this) {
                if (it.isNotEmpty()) {
                    AppUtility.progressBarDissMiss()
                    val getAddress: HashMap<String, Any>? = it

                   /**for edit case*/
                    if (it["isTrue"] == "true") {


                        myLocationResponse?.Location?.GPSX =
                            getAddress?.get("latitude") as Double

                        myLocationResponse?.Location?.GPSY =
                            getAddress["longitude"] as Double

                        myLocationResponse?.Location?.State =
                            getAddress["state"]?.toString()

                        myLocationResponse?.Location?.Suburb =
                            getAddress["suburb"].toString()


                        myLocationResponse?.Location?.Postcode =
                            getAddress["postcode"].toString()

                        myLocationResponse?.Location?.Street =
                            getAddress["address"].toString()


                        myLocationResponse?.Location?.StreetNumber =
                            getAddress["streetNumber"].toString()


                    } else {

                        lattitude = getAddress?.get("latitude") as Double

                        longitude = getAddress["longitude"] as Double

                        state = getAddress["state"]?.toString()

                        suburb = getAddress["suburb"].toString()

                        country = getAddress["country"].toString()

                        postCode = getAddress["postcode"].toString()

                        street = getAddress["address"].toString()

                        streetNumber= getAddress["streetNumber"].toString()


                    }
                }
            }
            /**add when find add from find me*/
            viewModel.googleSuccessUsingLatLang()?.observe(this) {
                if (it.isNotEmpty()) {
                    AppUtility.progressBarDissMiss()
                    val getAddress: HashMap<String, Any>? = it
                    if (it["isTrue"] == "true") {

                        myLocationResponse?.Location?.GPSX =
                            getAddress?.get("latitude") as Double

                        myLocationResponse?.Location?.GPSY =
                            getAddress["longitude"] as Double

                        myLocationResponse?.Location?.State =
                            getAddress["state"]?.toString()

                        myLocationResponse?.Location?.Suburb =
                            getAddress["suburb"].toString()


                        myLocationResponse?.Location?.Postcode =
                            getAddress["postcode"].toString()

                        myLocationResponse?.Location?.Street =
                            getAddress["address"].toString()


                        myLocationResponse?.Location?.StreetNumber =
                            getAddress["streetNumber"].toString()


                    } else {

                        lattitude = getAddress?.get("latitude") as Double

                        longitude = getAddress["longitude"] as Double

                        state = getAddress["state"]?.toString()

                        suburb = getAddress["suburb"].toString()

                        country = getAddress["country"].toString()

                        postCode = getAddress["postcode"].toString()

                        street = getAddress["address"].toString()

                        streetNumber= getAddress["streetNumber"].toString()


                    }
                }
            }


        }
    }


    private fun showEditDataView(myLocationResponse: MyLocationResAndEditLocationReq?) {
        binding.name.setText(myLocationResponse?.Location?.ContactName)
        binding.email.setText(myLocationResponse?.Location?.Email)
        binding.phone.setText(myLocationResponse?.Location?.Phone)
        binding.address.text = myLocationResponse?.Location?.Street
        binding.pickupCheckBox.isChecked = myLocationResponse?.DefaultPickup == true
        binding.dropOffCheckBox.isChecked = myLocationResponse?.DefaultDropoff == true
        binding.company.setText(myLocationResponse?.Location?.CompanyName)
        binding.unit.setText(myLocationResponse?.Location?.UnitNumber)
       binding.notes.setText(myLocationResponse?.Location?.Notes)
    }

    private fun onOkClick() {
        viewModel.deleteLocation(myLocationResponse?.PreferredLocationId)
    }

    private fun onCancelClick() {

    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.address -> {
                if (!Places.isInitialized()) {
                    val apiKey = getString(R.string.google_api_ke)
                    Places.initialize(applicationContext, apiKey)
                }

                try {
                    val fields =
                        listOf(
                            Place.Field.ADDRESS, Place.Field.LAT_LNG
                        )
                    val intent = Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields
                    )
                        .setCountry("AU")
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .build(this)

                    startActivityForResult(intent, placeAutocompleteRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            R.id.remove_txt -> {
                DialogActivity.logoutDialog(
                    this,
                    "Confirmation !",
                    "Are you sure you want to delete this location?",
                    "Ok", "Cancel",
                    onCancelClick = ::onCancelClick,
                    onOkClick = ::onOkClick
                )

            }
            R.id.find_me -> {
                getLocationClass?.getCurrentLocation(onAddress = ::getAddress)
            }
            R.id.back_btn -> {
                finish()
            }
            R.id.save_change_btn -> {
                if (isEdit) {

                    val address = if(binding.unit.text.toString().trim().isNullOrBlank())
                        myLocationResponse?.Location?.Street.toString()
                    else
                        binding.unit.text.toString().trim()+"/"+ myLocationResponse?.Location?.Street.toString()


                    myLocationResponse?.DefaultPickup = binding.pickupCheckBox.isChecked
                    myLocationResponse?.DefaultDropoff = binding.dropOffCheckBox.isChecked

                    myLocationResponse?.Location?.Address = address

                    myLocationResponse?.Location?.ContactName =
                        binding.name.text.toString().trim()
                    myLocationResponse?.Location?.Email =
                        binding.email.text.toString().trim()
                    myLocationResponse?.Location?.Phone =
                        binding.phone.text.toString().trim()
                    myLocationResponse?.Location?.Notes =
                        binding.notes.text.toString().trim()
                    myLocationResponse?.Location?.UnitNumber =
                        binding.unit.text.toString().trim()
                    myLocationResponse?.Location?.CompanyName =
                        binding.company.text.toString().trim()
                    if (checkValidation(
                            binding.name.text.toString().trim(),
                            binding.phone.text.toString().trim(),
                            binding.address.text.toString().trim()
                        )
                    )
                    viewModel.editLocation(myLocationResponse)
                } else {

                    val address = if(binding.unit.text.toString().trim().isNullOrBlank())
                        street.toString()
                    else
                        binding.unit.text.toString().trim()+"/"+street.toString()


                    val location2 = AddLocationReq.Location2(
                        address,
                        binding.company.text.toString().trim(),
                        binding.name.text.toString().trim(),
                        country,
                        binding.email.text.toString().trim(),
                        lattitude,
                        longitude,
                        binding.notes.text.toString().trim(),
                        binding.phone.text.toString().trim(),
                        postCode,
                        state,
                        street,
                        streetNumber,
                        suburb, binding.unit.text.toString().trim()
                    )



                    addLocationReq = AddLocationReq(
                        binding.dropOffCheckBox.isChecked,
                        binding.pickupCheckBox.isChecked, location2
                    )

                    if (checkValidation(
                            binding.name.text.toString().trim(),
                            binding.phone.text.toString().trim(),
                            binding.address.text.toString().trim()
                        )
                    )
                        viewModel.addLocation(addLocationReq)
                }
            }
        }
    }

    private fun checkValidation(name: String, phone: String, address: String): Boolean {
        if (name == "" && phone == "" && address == "") {
            AppUtility.validateEditTextField(
                binding.name, "Please enter a contact name & business."
            )
            AppUtility.validateEditTextField(
                binding.phone,
                "Please enter a valid mobile number."
            )
            AppUtility.validateTextField(binding.address, "Please enter address.")
            return false
        } else if (name == "") {
            AppUtility.validateEditTextField(
                binding.name, "Please enter a contact name & business."
            )
            return false
        } else if (phone == "") {
            AppUtility.validateEditTextField(
                binding.phone,
                "Please enter a valid mobile number."
            )
            return false
        } else if (address == "") {
            AppUtility.validateTextField(binding.address, "Please enter address.")
            return false
        }
        return true
    }


    private fun getAddress(lat: Double, lang: Double) {

        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lang, 1)
        binding.address.text = address[0].getAddressLine(0)
        viewModel.addFromGoogleLatLang(lat.toString(), lang.toString(), isEdit)
    }

    override fun onBackPressed() {
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == placeAutocompleteRequest) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    binding.address.text = place.address
                    viewModel.addFromGoogleAdd(place.address, isEdit)

                }
                AutocompleteActivity.RESULT_ERROR -> {
                    binding.address.text = ""
                }
                RESULT_CANCELED -> {
                    Log.i("Place API Failure", "  -------------- User Cancelled -------------")
                }
            }
        }
    }


}


