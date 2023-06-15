package com.zoom2u_customer.ui.application.bottom_navigation.history.history_details

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.apiclient.GetAddressFromGoogleAPI
import com.zoom2u_customer.apiclient.GoogleServiceApi
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityHistoryDetailsBinding
import com.zoom2u_customer.ui.DocItemShowAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.history.HistoryResponse
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.BookingResponse
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.RouteParser
import org.json.JSONException


class HistoryDetailsActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    private var arrayCourierPick: List<String>? = null
    private var arrayCourierDrop: List<String>? = null
    private lateinit var map: GoogleMap
    var response: HistoryDetailsResponse? = null
    private var historyItem: HistoryResponse? = null
    lateinit var viewModel: HistoryDetailsViewModel
    private var repositoryGoogleAddress: GoogleAddressRepository? = null
    private var repository: HistoryDetailsRepository? = null
    lateinit var binding: ActivityHistoryDetailsBinding
    private var bookingResponse: BookingResponse? = null
    private var docAdapter: DocItemShowAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_details)

        viewModel = ViewModelProvider(this)[HistoryDetailsViewModel::class.java]
        val googleServiceApi: GoogleServiceApi = GetAddressFromGoogleAPI.getGoogleServices()
        val serviceApi: ServiceApi = getServices()
        repositoryGoogleAddress = GoogleAddressRepository(googleServiceApi, this)
        repository = HistoryDetailsRepository(serviceApi, this)
        viewModel.repository = repository
        viewModel.repositoryGoogleAddress = repositoryGoogleAddress

        if (intent.hasExtra("HistoryItem")) {
            historyItem = intent.getParcelableExtra("HistoryItem")
            viewModel.setHistoryDetails(historyItem?.BookingRef)
        } else if (intent.hasExtra("BookingRef")) {
            bookingResponse = intent.getParcelableExtra("BookingRef")
            viewModel.setHistoryDetails(bookingResponse?.BookingRef)
        }

        viewModel.getHistoryDetails()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                setDataToView(it)
            }
        }
        val fragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)


        viewModel.getRouteSuccess().observe(this) {
            if (!it.isNullOrEmpty())
                RouteParser.parserTask(this, map, it)
        }

        viewModel.getCancelBooking().observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                val intent = Intent()
                historyItem?.IsCancel = true
                intent.putExtra("historyItem", historyItem)
                setResult(85, intent)
                Toast.makeText(this, "Booking cancellation successfully.", Toast.LENGTH_SHORT)
                    .show()
                finish()

            }

        }




        viewModel.getCancelBookingMAB().observe(this) {
            if (it != null) {
                if (it == "true") {
                    AppUtility.progressBarDissMiss()
                    if (intent.hasExtra("BookingRef")) {
                        Toast.makeText(
                            this,
                            "Booking cancellation successfully.",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        finish()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToView(it: HistoryDetailsResponse) {
        this.response = it



            /**show more less option*/
            if (it.DeliveryShipments!!.size > 2) {
                binding.more.visibility = View.VISIBLE
            }
            setAdapterView(this, it)

        arrayCourierPick = it.PickupLocation?.split(",")
        arrayCourierDrop = it.DropLocation?.split(",")
        initializeMap()
        binding.pickAdd.text = it.PickupAddress
        binding.pickName.text = it.PickupContactName
        binding.pickPhone.text = it.PickupPhone
        binding.pickEmail.text = it.PickupEmail

        val pickDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(it.PickupDateTime)
        val pickUpDateTimeSplit: Array<String>? = pickDateTime?.split(" ")?.toTypedArray()
        binding.pickDateTime.text =
            pickUpDateTimeSplit?.get(1) + " " + pickUpDateTimeSplit?.get(2) + " | " + pickUpDateTimeSplit?.get(
                0
            )

        val dropDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(it.DropDateTime)
        val dropUpDateTimeSplit: Array<String>? = dropDateTime?.split(" ")?.toTypedArray()
        binding.dropDateTime.text =
            dropUpDateTimeSplit?.get(1) + " " + dropUpDateTimeSplit?.get(2) + " | " + dropUpDateTimeSplit?.get(
                0
            )


        if(it.BookingRef?.get(0)==('H')  &&
            it.BookingRef?.get(1)==('Q')) {
            val pickTimeReq = AppUtility.getDateTimeFromDeviceToServerForDate(it.RequestedPickupDateTimeWindowStart)
            val pickTimeReqEnd = AppUtility.getDateTimeFromDeviceToServerForDate(it.RequestedPickupDateTimeWindowEnd)
            val pickUpReqSplit: Array<String>? = pickTimeReq?.split(" ")?.toTypedArray()
            val pickUpReqSplitEnd: Array<String>? = pickTimeReqEnd?.split(" ")?.toTypedArray()
            binding.pickTimeReq.text ="between "+pickUpReqSplit?.get(1) + " " + pickUpReqSplit?.get(2) +" and "+pickUpReqSplitEnd?.get(1) + " " + pickUpReqSplitEnd?.get(2) +" | " + pickUpReqSplit?.get(0)

            val dropTimeReq = AppUtility.getDateTimeFromDeviceToServerForDate(it.RequestedDropDateTimeWindowStart)
            val dropTimeReqEnd = AppUtility.getDateTimeFromDeviceToServerForDate(it.RequestedDropDateTimeWindowEnd)
            val dropReqSplit: Array<String>? = dropTimeReq?.split(" ")?.toTypedArray()
            val dropReqSplitEnd: Array<String>? = dropTimeReqEnd?.split(" ")?.toTypedArray()
            binding.dropTimeReq.text = "between "+dropReqSplit?.get(1) + " " + dropReqSplit?.get(2) +" and "+ dropReqSplitEnd?.get(1) + " " + dropReqSplitEnd?.get(2)+" | " + dropReqSplit?.get(0)
        }else{
            val pickTimeReq = AppUtility.getDateTimeFromDeviceToServerForDate(it.PickupDateTime)
            val pickUpReqSplit: Array<String>? = pickTimeReq?.split(" ")?.toTypedArray()
            binding.pickTimeReq.text = pickUpReqSplit?.get(1) + " " + pickUpReqSplit?.get(2) + " | " + pickUpReqSplit?.get(0)
            val dropTimeReq = AppUtility.getDateTimeFromDeviceToServerForDate(it.DropDateTime)
            val dropReqSplit: Array<String>? = dropTimeReq?.split(" ")?.toTypedArray()
            binding.dropTimeReq.text = dropReqSplit?.get(1) + " " + dropReqSplit?.get(2) + " | " + dropReqSplit?.get(0)
        }


        binding.dropAdd.text = it.DropAddress
        binding.dropName.text = it.DropContactName
        binding.dropPhone.text = it.DropPhone
        binding.dropEmail.text = it.DropEmail
        binding.dropAdd.text = it.DropAddress
        binding.refId.text = it.BookingRef
        //binding.price.text = "$" + it.Price.toString()


        if (!TextUtils.isEmpty(it.PackageNotes))
            binding.packageNote.text = it.PackageNotes
        else
            binding.packageNote.text = "No package notes available."


        if (!TextUtils.isEmpty(it.PickupNotes)) {
            binding.deliveryNote.text = it.PickupNotes
        } else {
            binding.deliveryNote.text = "No delivery notes available"
        }


        if (!TextUtils.isEmpty(it.PackageImage)) {
            binding.pickImage.setImageBitmap(AppUtility.getBitmapFromURL(it.PackageImage))
        }


        if (!TextUtils.isEmpty(it.PickupSignature)) {
            binding.pickSignature.setImageBitmap(AppUtility.getBitmapFromURL(it.PickupSignature))
        }
        if (!TextUtils.isEmpty(it.DropSignature)) {
            binding.dropSignature.setImageBitmap(AppUtility.getBitmapFromURL(it.DropSignature))
        }

        if (!TextUtils.isEmpty(it.DropPhoto)) {
            binding.dropImage.setImageBitmap(AppUtility.getBitmapFromURL(it.DropPhoto))
        }


        if (it.IsCancel == true && it.IsOnHold==false) {
            binding.cancelBook.visibility = View.GONE
            binding.price.text = "No Charge"
            binding.status.text = "Cancelled"
            binding.name.text="-"
            binding.status.setBackgroundColor(Color.parseColor("#ff0000"))
            binding.status.setTextColor(Color.WHITE)
        } else if (it.IsOnHold == true &&it.IsCancel == false) {
            binding.cancelBook.visibility = View.VISIBLE
            binding.price.text = "$" + it.Price.toString()
            binding.status.text = "On Hold"
            binding.name.text="Awaiting confirmation"
            binding.status.setBackgroundColor(Color.parseColor("#ff0000"))
            binding.status.setTextColor(Color.WHITE)
        }
        else if (it.IsOnHold == true &&it.IsCancel == true) {
            binding.cancelBook.visibility = View.VISIBLE
            binding.price.text = "No Charge"
            binding.status.text = "On Hold"
            binding.name.text="-"
            binding.status.setBackgroundColor(Color.parseColor("#ff0000"))
            binding.status.setTextColor(Color.WHITE)
        } else {
            binding.name.text=it.CourierFirstName+" "+it.CourierLastName
            setStatus(it.Status)
            binding.price.text = "$" + it.Price.toString()
        }


        /*if(!TextUtils.isEmpty(it.PickP)) {
            binding.dropImage.setImageBitmap(AppUtility.getBitmapFromURL(it.DropPhoto))
        }*/

        binding.pickSignature.setOnClickListener(this)
        binding.dropSignature.setOnClickListener(this)
        binding.dropImage.setOnClickListener(this)
        binding.backBtn.setOnClickListener(this)
        binding.cancelBook.setOnClickListener(this)
        binding.card4.setOnClickListener(this)
        binding.pickImage.setOnClickListener(this)
        binding.more.setOnClickListener(this)
        binding.less.setOnClickListener(this)
       // binding.status.setOnClickListener(this)
        /**condition for actual pick*/
        if (it.PickupActual.isNullOrEmpty()) {
            binding.pickTimeActual.visibility = View.INVISIBLE
            binding.pickActual.visibility = View.INVISIBLE
        }
        else {
            val pickActualReq = AppUtility.getDateTimeFromDeviceToServerForDate(it.PickupActual)
            val pickActualSplit: Array<String>? = pickActualReq?.split(" ")?.toTypedArray()
            binding.pickTimeActual.text = pickActualSplit?.get(1) + " " + pickActualSplit?.get(2) + " | " + pickActualSplit?.get(0)
        }

        /**condition for actual drop*/
        if (it.DropActual.isNullOrEmpty()) {
            binding.dropTimeActual.visibility = View.INVISIBLE
            binding.dropActual.visibility = View.INVISIBLE
        }
        else {
            val dropActualReq = AppUtility.getDateTimeFromDeviceToServerForDate(it.DropActual)
            val dropActualSplit: Array<String>? = dropActualReq?.split(" ")?.toTypedArray()
            binding.dropTimeActual.text = dropActualSplit?.get(1) + " " + dropActualSplit?.get(2) + " | " + dropActualSplit?.get(0)

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setStatus(status: String?) {
        when (status) {
            "Accepted" -> {
                binding.cancelBook.visibility = View.VISIBLE
                binding.status.text = "Allocated"
                binding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                binding.status.setTextColor(Color.BLACK)
            }
            "Picked up" -> {
                binding.cancelBook.visibility = View.GONE
                binding.status.text = "Picked up"
                binding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                binding.status.setTextColor(Color.WHITE)
            }
            "Dropped Off" -> {
                binding.cancelBook.visibility = View.GONE
                binding.status.text = "Delivered"
                binding.status.setBackgroundColor(Color.parseColor("#76D750"))
                binding.status.setTextColor(Color.BLACK)
            }
            "On Route to Pickup" -> {
                binding.cancelBook.visibility = View.GONE
                binding.status.text = "On Route to-Pickup"
                binding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                binding.status.setTextColor(Color.WHITE)
            }
            "On Route to Dropoff" -> {
                binding.cancelBook.visibility = View.GONE
                binding.status.text = "On Route to-Dropoff"
                binding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                binding.status.setTextColor(Color.WHITE)
            }
            "Returned"  ->{
                binding.status.text ="Returned"
                binding.status.setBackgroundColor(Color.parseColor("#ff0000"))
                binding.status.setTextColor(Color.WHITE)
            }
            "Tried to deliver" -> {
                binding.cancelBook.visibility = View.GONE
                binding.status.text ="Tried to deliver"
                binding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                binding.status.setTextColor(Color.WHITE) }
            else -> {
                binding.cancelBook.visibility = View.VISIBLE
                binding.status.text = "Accepted"
                binding.name.text="Awaiting confirmation"
                binding.status.setBackgroundColor(Color.parseColor("#FFD100"))
                binding.status.setTextColor(Color.BLACK)
            }
        }

    }



    fun setAdapterView(context: Context?, historyDetailsResponse: HistoryDetailsResponse) {
       if(historyDetailsResponse.DeliveryShipments?.size!=0) {
           binding.ll1.visibility=View.GONE
           binding.doc.visibility=View.VISIBLE
           val layoutManager1 = GridLayoutManager(this, 2)
           binding.docRecycler.layoutManager = layoutManager1
           (binding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
           docAdapter = DocItemShowAdapter(context, historyDetailsResponse.DeliveryShipments!!)
           binding.docRecycler.adapter = docAdapter
       }else{
           binding.ll1.visibility=View.VISIBLE
           binding.doc.visibility=View.GONE
       }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.pick_signature -> {
                if (!TextUtils.isEmpty(response?.PickupSignature))
                    AppUtility.fullSizeImageView(this, response?.PickupSignature.toString())
                else
                    Toast.makeText(this, "No pickup signature found.", Toast.LENGTH_SHORT).show()

            }
            R.id.drop_signature -> {
                if (!TextUtils.isEmpty(response?.DropSignature))
                    AppUtility.fullSizeImageView(this, response?.DropSignature.toString())
                else
                    Toast.makeText(this, "No dropoff signature found.", Toast.LENGTH_SHORT).show()

            }
            R.id.more -> {
                docAdapter?.isMoreEnable(true)
                binding.more.visibility=View.GONE
                binding.less.visibility=View.VISIBLE
            }
            R.id.less -> {
                docAdapter?.isMoreEnable(false)
                binding.more.visibility=View.VISIBLE
                binding.less.visibility=View.GONE

            }
            R.id.drop_image -> {
                if (!TextUtils.isEmpty(response?.PickupSignature))
                    AppUtility.fullSizeImageView(this, response?.DropPhoto.toString())
                else
                    Toast.makeText(this, "No dropoff image found.", Toast.LENGTH_SHORT).show()
            }
            R.id.pick_image -> {
                if (!TextUtils.isEmpty(response?.PackageImage))
                    AppUtility.fullSizeImageView(this, response?.PackageImage.toString())
                else
                    Toast.makeText(this, "No pickup image found.", Toast.LENGTH_SHORT).show()
            }
            R.id.back_btn -> {
                finish()
            }
            R.id.cancel_book -> {
                DialogActivity.logoutDialog(
                    this,
                    "Confirm!",
                    "Are you sure you want to cancel your booking?",
                    "Yes", "No",
                    onCancelClick = ::onNoClick,
                    onOkClick = ::onYesClick
                )
            }
            R.id.card4 -> {
                 if(response?.TrackingLink!=null) {
                     val browserIntent: Intent = Intent(
                         Intent.ACTION_VIEW,
                         Uri.parse(response?.TrackingLink)
                     )
                     startActivity(browserIntent)
                 }
            }
          /*  R.id.status->{
                if(response?.IsOnHold==true){
                    val intentOnHold = Intent(this, OnHoldActivity::class.java)
                    intentOnHold.putExtra("historyDetailsResponse", response)
                    intentOnHold.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivityForResult(intentOnHold,85)
                }
            }*/
        }
    }

    private fun onNoClick() {

    }

    private fun onYesClick() {
        if (intent.hasExtra("BookingRef"))
            viewModel.cancelBooking(bookingResponse?.BookingId)
        else
            viewModel.cancelBooking(historyItem)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        //hide zoom in out button in map
        map.uiSettings.isZoomControlsEnabled = false
        //googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        //googleMap.setMyLocationEnabled(true);
       // map.uiSettings.isZoomControlsEnabled = true
        // Enable / Disable my location button
        map.uiSettings.isMyLocationButtonEnabled = true
        // Enable / Disable Compass icon
        map.uiSettings.isCompassEnabled = true
        // Enable / Disable Rotate gesture
        map.uiSettings.isRotateGesturesEnabled = true
        // Enable / Disable zooming functionality
        map.uiSettings.isZoomGesturesEnabled = true

    }

    private fun initializeMap() {

        try {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        arrayCourierPick?.get(0)!!.toDouble(),
                        arrayCourierPick?.get(1)!!.toDouble()
                    ), 10F
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        addMarkers1()
        try {
            // Getting URL to the Google Directions API
           /* if (response?.Status == "Accepted" || response?.Status == "On Route to Pickup") {
                val url: String? = getDirectionsUrl(
                    LatLng(
                        response?.Latitude?.toDouble()!!,
                        response?.Longitude?.toDouble()!!
                    ),
                    LatLng(
                        arrayCourierPick?.get(0)!!.toDouble(),
                        arrayCourierPick?.get(1)!!.toDouble()
                    )
                )
                viewModel.getRoute(url)
                val url1: String? = getDirectionsUrl(
                    LatLng(
                        arrayCourierPick?.get(0)!!.toDouble(),
                        arrayCourierPick?.get(1)!!.toDouble()
                    ),
                    LatLng(
                        arrayCourierDrop?.get(0)!!.toDouble(),
                        arrayCourierDrop?.get(1)!!.toDouble()
                    )
                )
                viewModel.getRoute(url1)
            } else if (response?.Status == "Picked up" || response?.Status == "On Route to Dropoff" || response?.Status == "Tried to deliver"
            ) {
                val url2: String? = getDirectionsUrl(
                    LatLng(
                        response?.Latitude!!.toDouble(),
                        response?.Longitude!!.toDouble()
                    ),
                    LatLng(
                        arrayCourierDrop?.get(0)!!.toDouble(),
                        arrayCourierDrop?.get(1)!!.toDouble()
                    )
                )
                viewModel.getRoute(url2)
            } else {*/
                val url3: String? = getDirectionsUrl(
                    LatLng(
                        arrayCourierPick?.get(0)!!.toDouble(),
                        arrayCourierPick?.get(1)!!.toDouble()
                    ),
                    LatLng(
                        arrayCourierDrop?.get(0)!!.toDouble(),
                        arrayCourierDrop?.get(1)!!.toDouble()
                    )
                )
                viewModel.getRoute(url3)
          //  }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String? {
        // Origin of route
        val str_origin =
            "origin=" + origin.latitude.toString() + "," + origin.longitude

        // Destination of route
        val str_dest =
            "destination=" + dest.latitude.toString() + "," + dest.longitude

        // Sensor enabled
        val sensor =
            "sensor=false&key=" + "AIzaSyDXy3Z6OzAQ3siNfARS3Y54-sbhNQSBL0U" // Purchased account zoom.2ua@gmail.com


        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$sensor"

        // Output format
        val output = "json"

        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

/*
    private fun addMarkers() {
        try {
            if (response?.Status == "Accepted" || response?.Status == "On Route to Pickup" || response?.Status == "On Route to Dropoff") {
                when (response?.Vehicle) {
                        "Van" -> {
                            map?.addMarker(MarkerOptions().position(LatLng(response?.Latitude!!.toDouble(), response?.Longitude!!.toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.icontruck)))
                            map?.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
                            map?.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                        }
                        "Bike" -> {
                            map?.addMarker(MarkerOptions().position(LatLng(response?.Latitude!!.toDouble(), response?.Longitude!!.toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconbike)))
                            map?.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
                            map?.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                        }
                        "Car" -> {
                            map?.addMarker(MarkerOptions().position(LatLng(response?.Latitude!!.toDouble(), response?.Longitude!!.toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconcar)))
                            map?.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
                            map?.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon))
                            )
                        }
                    }
                }
             else if (response?.Status == "Picked up" || response?.Status == "Tried to deliver") {
                when (response?.Vehicle) {
                    "Van" -> {
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.icontruck)))
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                    }
                    "Bike" -> {
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconbike)))
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                    }
                    "Car" -> {
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconcar)))
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                    }
                }
            } else {
                when (response?.Vehicle) {
                    "Van" -> {
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                    }
                    "Bike" -> {
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                    }
                    "Car" -> {
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
                        map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }*/


    private fun addMarkers1() {
        try {
            map.addMarker(MarkerOptions().position(LatLng(arrayCourierPick!![0].toDouble(), arrayCourierPick!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)))
            map.addMarker(MarkerOptions().position(LatLng(arrayCourierDrop!![0].toDouble(), arrayCourierDrop!![1].toDouble())).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


}
