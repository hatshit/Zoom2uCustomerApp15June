package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.GetAddressFromGoogle.GoogleAddressRepository
import com.zoom2u_customer.apiclient.GetAddressFromGoogleAPI
import com.zoom2u_customer.apiclient.GoogleServiceApi
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityActiveBidBinding
import com.zoom2u_customer.ui.application.bottom_navigation.base_page.BasePageActivity
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListRepository
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.BidOffersFragment
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidChatList
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidLoadChatBookingArray
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import com.zoom2u_customer.utility.RouteParser
import org.json.JSONException


class ActiveBidActivity : AppCompatActivity(), OnMapReadyCallback {
    private var activeBidItem: ActiveBidListResponse? = null
    private var ItemType: String? = null
    lateinit var binding: ActivityActiveBidBinding
    private var quoteID: Int? = null
    private var heavyFreightQuoteRef: String? = null
    private lateinit var map: GoogleMap
    private lateinit var viewpageradapter: BidViewPagerAdapter
    lateinit var viewModel: BidDetailsViewModel
    private var repositoryGoogleAddress: GoogleAddressRepository? = null
    private var repository: BidDetailsRepository? = null
    private var repositoryActive: ActiveBidListRepository? = null
    private var bidDetailsResponse:BidDetailsResponse?=null
    private var heavyFreightDetailsResponse:HeavyFreightBidDetails?=null
    private var pos:Int?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_active_bid)

        viewModel = ViewModelProvider(this)[BidDetailsViewModel::class.java]
        val googleServiceApi: GoogleServiceApi = GetAddressFromGoogleAPI.getGoogleServices()
        val serviceApi: ServiceApi = ApiClient.getServices()
        repositoryGoogleAddress = GoogleAddressRepository(googleServiceApi, this)
        repository = BidDetailsRepository(serviceApi, this)
        repositoryActive = ActiveBidListRepository(serviceApi, this)
        viewModel.repositoryActive = repositoryActive
        viewModel.repository = repository
        viewModel.repositoryGoogleAddress = repositoryGoogleAddress

        when {
            intent.hasExtra("ActiveBidItem") -> {
                activeBidItem = intent.getParcelableExtra("ActiveBidItem")
                quoteID = activeBidItem?.Id
                ItemType =activeBidItem?.ItemType
                pos = intent.getStringExtra("pos")?.toInt()
                if(activeBidItem?.ItemType!="Freight")
                    viewModel.getBidDetails(quoteID)
                else {
                    heavyFreightQuoteRef = activeBidItem?.QuoteRef
                    viewModel.getHeavyFreightBidDetails(heavyFreightQuoteRef)
                }


            }
            intent.hasExtra("QuoteId1") -> {
                quoteID = intent.getStringExtra("QuoteId1")?.toInt()
                //binding.cancel.visibility= View.GONE
                viewModel.getBidDetails(quoteID)
            }
            intent.hasExtra("HeavyFreightQuoteRef") -> {
                heavyFreightQuoteRef = intent.getStringExtra("HeavyFreightQuoteRef")
                activeBidItem = intent.getParcelableExtra<ActiveBidListResponse>("ActiveBidItemFromHeavyFreight")
                //binding.cancel.visibility= View.GONE
                viewModel.getHeavyFreightBidDetails(heavyFreightQuoteRef)
            }
        }




        val fragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        fragment.getMapAsync(this)

        viewModel.getBidDetailsSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                bidDetailsResponse=it

                if(bidDetailsResponse?.Offers?.size!! >0){
                    val arrayOfChatDelivery: MutableList<ActiveBidChatList> = ArrayList()
                    for (i in 0 until bidDetailsResponse?.Offers?.size!!) {
                        val chatList =  ActiveBidChatList(this, bidDetailsResponse?.Offers!![i],bidDetailsResponse)
                        arrayOfChatDelivery.add(chatList)
                    }
                    ActiveBidLoadChatBookingArray(arrayOfChatDelivery.toMutableList())
                }


                val arrayCourierPick: MutableList<String> = ArrayList()
                val arrayCourierDrop: MutableList<String> = ArrayList()
                arrayCourierPick.add(it.PickupLatitude.toString())
                arrayCourierPick.add(it.PickupLongitude.toString())
                arrayCourierDrop.add(it.DropLatitude.toString())
                arrayCourierDrop.add(it.DropLongitude.toString())

                initializeMap(arrayCourierPick,arrayCourierDrop)

                viewpageradapter =BidViewPagerAdapter(
                    supportFragmentManager,
                    it,
                    null,
                    activeBidItem
                )
                binding.pager.adapter = viewpageradapter
                binding.tabLayout.setupWithViewPager(binding.pager)
            }
        }


        viewModel.getHeavyFreightBidDetailsSuccess()?.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()

                heavyFreightDetailsResponse=it

                if(heavyFreightDetailsResponse?.Offers?.size!! >0){
                    val arrayOfChatDelivery: MutableList<ActiveBidChatList> = ArrayList()
                    for (i in 0 until heavyFreightDetailsResponse?.Offers?.size!!) {
                        val chatList =  ActiveBidChatList(this, heavyFreightDetailsResponse?.Offers!![i],heavyFreightDetailsResponse)
                        arrayOfChatDelivery.add(chatList)
                    }
                    ActiveBidLoadChatBookingArray(arrayOfChatDelivery.reversed().toMutableList())
                }

                val arrayCourierPick: MutableList<String> = ArrayList()
                val arrayCourierDrop: MutableList<String> = ArrayList()
                arrayCourierPick.add(it.PickupLatitude.toString())
                arrayCourierPick.add(it.PickupLongitude.toString())
                arrayCourierDrop.add(it.DropLatitude.toString())
                arrayCourierDrop.add(it.DropLongitude.toString())

                initializeMap(arrayCourierPick,arrayCourierDrop)

                viewpageradapter =BidViewPagerAdapter(supportFragmentManager, null, it,activeBidItem )
                binding.pager.adapter = viewpageradapter
                binding.tabLayout.setupWithViewPager(binding.pager)
            }
        }


        viewModel.getRouteSuccess().observe(this) {
            if (!it.isNullOrEmpty())
                RouteParser.parserTask(this,map,it)
        }

        binding.backBtn.setOnClickListener{
            finish()
        }

        binding.cancel.setOnClickListener{
            DialogActivity.logoutDialog(
                this,
                "Confirm!",
                "Are you sure you want to cancel your bid request?",
                "Yes", "No",
                onCancelClick = ::onNoClick,
                onOkClick = ::onYesClick
            )
        }



        viewModel.getBidCancelSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                Toast.makeText(this, "Bid delete successfully.", Toast.LENGTH_LONG).show()
                if(it == "QuoteId1"){
                    val intent1 = Intent("bid_refresh1")
                    LocalBroadcastManager.getInstance(this@ActiveBidActivity).sendBroadcast(intent1)
                    val intent = Intent(this, BasePageActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    finish()
                }else {
                    val intent = Intent()
                    intent.putExtra("pos",it)
                    setResult(3, intent)
                    finish()
                }            }
        }


        viewModel.getHeavyBidCancelSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                Toast.makeText(this, "Bid delete successfully.", Toast.LENGTH_LONG).show()
                if(it==("HeavyFreightQuoteRef")){
                    val intent1 = Intent("bid_refresh1")
                    LocalBroadcastManager.getInstance(this@ActiveBidActivity).sendBroadcast(intent1)
                    val intent = Intent(this, BasePageActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent()
                    intent.putExtra("pos",it)
                    setResult(3, intent)
                    finish()
            }
            }
        }


    }
    private fun onNoClick() {}
    private fun onYesClick() {
        if (intent.hasExtra("QuoteId1")) {
            viewModel.getBidCancel(bidDetailsResponse?.Id, "QuoteId1")
        } else if (intent.hasExtra("HeavyFreightQuoteRef")) {
            viewModel.getHeavyBidCancel(heavyFreightDetailsResponse?.QuoteRequestId, "HeavyFreightQuoteRef")
        } else {
            if (ItemType == "Freight")
                viewModel.getHeavyBidCancel(activeBidItem?.Id, pos.toString())
            else if (ItemType == "ExtraLargeItem" || ItemType == "SuggestPrice")
                viewModel.getBidCancel(bidDetailsResponse?.Id, pos.toString())
        }
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

    private fun initializeMap(
        arrayCourierPick: MutableList<String>,
        arrayCourierDrop: MutableList<String>
    ) {

        try {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        arrayCourierPick[0].toDouble(),
                        arrayCourierPick[1].toDouble()
                    ), 10F
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        -33.8619486,
                        151.00586
                    ), 12F
                )
            )
        }
        addMarkers(arrayCourierPick,arrayCourierDrop)
        try {
            // Getting URL to the Google Directions API

            val url3: String? = getDirectionsUrl(
                LatLng(
                    arrayCourierPick[0].toDouble(),
                    arrayCourierPick[1].toDouble()
                ),
                LatLng(
                    arrayCourierDrop[0].toDouble(),
                    arrayCourierDrop[1].toDouble()
                )
            )
            viewModel.getRoute(url3)

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

    private fun addMarkers(
        arrayCourierPick: MutableList<String>,
        arrayCourierDrop: MutableList<String>
    ) {
        try {

            map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        arrayCourierPick[0].toDouble(),
                        arrayCourierPick[1].toDouble()
                    )
                ).title("").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_pickup_icon)
                )
            )
            map.addMarker(
                MarkerOptions().position(
                    LatLng(
                        arrayCourierDrop[0].toDouble(),
                        arrayCourierDrop[1].toDouble()
                    )
                ).title("").icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_drop_off_icon)
                )
            )


        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            if(fragment is BidOffersFragment)
                fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    public fun updateOfferTabTitle(size:Int){
        binding.tabLayout.getTabAt(1)?.text = "Offers($size)"
    }

  /*  override fun onBackPressed() {
        val intent = Intent(this, BasePageActivity::class.java)
        intent.putExtra("openHome",true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }*/
}