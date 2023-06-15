package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.completed_bid_details

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.FragmentCompletedBidDetailsBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.CompletedHeavyFreightAdapter
import com.zoom2u_customer.ui.DocItemShowAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.ShowBidImageAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.CompletedBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.CompletedDetailsResponse
import com.zoom2u_customer.utility.AppUtility


class CompletedDetailsFragment() : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentCompletedBidDetailsBinding
    var bidDetail: CompletedDetailsResponse? = null
    private var imageAdapter: ShowBidImageAdapter?=null
    private var docAdapter: DocItemShowAdapter?=null
    private var heavyFreightBidDetails: HeavyFreightBidDetails? =null
    private var docItemHeavyFreightShowAdapter : CompletedHeavyFreightAdapter?=null
    private var completedBidItem: CompletedBidListResponse? = null

    companion object {
        private var completedBidItem1: CompletedBidListResponse? = null
        var bidDetail1: CompletedDetailsResponse? = null
        var heavyFreightBidDetails1: HeavyFreightBidDetails? =null
        fun newInstance(
            bidDetails: CompletedDetailsResponse?,
            heavyFreightBidDetails: HeavyFreightBidDetails?,
            completedBidItem: CompletedBidListResponse?
        ): CompletedDetailsFragment {
            this.bidDetail1 = bidDetails
            this.heavyFreightBidDetails1 = heavyFreightBidDetails
            this.completedBidItem1 = completedBidItem
            return CompletedDetailsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =

            FragmentCompletedBidDetailsBinding.inflate(inflater, container, false)

        this.bidDetail= bidDetail1
        this.heavyFreightBidDetails = heavyFreightBidDetails1
        this.completedBidItem = completedBidItem1
        if(bidDetail!=null){
            setDataToView(bidDetail)
            setAdapterView(container?.context)
        }
        else if(heavyFreightBidDetails!=null){
            setDataToView1(heavyFreightBidDetails)
            setAdapterView1(container?.context)
        }

        binding.more.setOnClickListener(this)
        binding.less.setOnClickListener(this)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToView(bidDetail: CompletedDetailsResponse?) {
        binding.pickAdd.text = bidDetail?.PickupLocation?.Address
        binding.dropAdd.text = bidDetail?.DropLocation?.Address

        val pickDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(bidDetail?.PickupDateTime)
        val pickUpDateTimeSplit: Array<String>? = pickDateTime?.split(" ")?.toTypedArray()
        binding.pickTime.text = pickUpDateTimeSplit?.get(1) + " " + pickUpDateTimeSplit?.get(2) + " | " + pickUpDateTimeSplit?.get(0)

        val dropDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(bidDetail?.DropDateTime)
        val dropUpDateTimeSplit: Array<String>? = dropDateTime?.split(" ")?.toTypedArray()
        binding.dropTime.text = dropUpDateTimeSplit?.get(1) + " " + dropUpDateTimeSplit?.get(2) + " | " + dropUpDateTimeSplit?.get(0)

        binding.offer.text=bidDetail?.Offers?.size.toString()
        binding.notes.text=bidDetail?.Notes

        /**show more less option*/
        if( bidDetail?.Shipments!!.size>2){
            binding.more.visibility=View.VISIBLE
        }

        binding.pickName.text = bidDetail.PickupLocation?.ContactName
        binding.pickPhone.text = bidDetail.PickupPhone
        binding.pickEmail.text = bidDetail.PickupEmail


        binding.dropName.text = bidDetail.DropLocation?.ContactName
        binding.dropPhone.text = bidDetail.DropPhone
        binding.dropEmail.text = bidDetail.DropEmail

        binding.distance.text = bidDetail.Distance
        binding.ref.text =bidDetail.QuoteRef
    }

    @SuppressLint("SetTextI18n")
    private fun setDataToView1(bidDetail: HeavyFreightBidDetails?) {
        binding.pickAdd.text = bidDetail?.PickupLocation?.Address
        binding.dropAdd.text = bidDetail?.DropLocation?.Address

        val pickDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(bidDetail?.PickupDateTime)
        val pickUpDateTimeSplit: Array<String>? = pickDateTime?.split(" ")?.toTypedArray()
        binding.pickTime.text = pickUpDateTimeSplit?.get(1) + " " + pickUpDateTimeSplit?.get(2) + " | " + pickUpDateTimeSplit?.get(0)

        val dropDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(bidDetail?.DropDateTime)
        val dropUpDateTimeSplit: Array<String>? = dropDateTime?.split(" ")?.toTypedArray()
        binding.dropTime.text = dropUpDateTimeSplit?.get(1) + " " + dropUpDateTimeSplit?.get(2) + " | " + dropUpDateTimeSplit?.get(0)

        binding.offer.text=bidDetail?.Offers?.size.toString()
        binding.notes.text=bidDetail?.Notes

        /**show more less option*/
        if( bidDetail?.Dimensions!!.size>2){
            binding.more.visibility=View.VISIBLE
        }

        binding.pickName.text = bidDetail.PickupLocation?.ContactName
        binding.pickPhone.text =  bidDetail.PickupLocation?.Phone
        binding.pickEmail.text =  bidDetail.PickupLocation?.Email


        binding.dropName.text = bidDetail.DropLocation?.ContactName
        binding.dropPhone.text = bidDetail.DropLocation?.Phone
        binding.dropEmail.text = bidDetail.DropLocation?.Email

        binding.distance.text = bidDetail.Distance+" "+"km"

        binding.ref.text = bidDetail.QuoteRef
    }

    fun setAdapterView(context: Context?) {
        if(bidDetail?.PackageImages!!.isNullOrEmpty()) {
           binding.defaultImage.visibility=View.VISIBLE
            binding.bidImages.visibility=View.GONE
        }else{
            binding.bidImages.visibility=View.VISIBLE
            binding.defaultImage.visibility=View.GONE
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.bidImages.layoutManager = layoutManager
            imageAdapter = ShowBidImageAdapter(
                context,
                bidDetail?.PackageImages!!,
                onItemClick = ::onItemClick
            )
            binding.bidImages.adapter = imageAdapter
        }


        val layoutManager1 = GridLayoutManager(activity, 2)
        binding.docRecycler.layoutManager = layoutManager1
        (binding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
         docAdapter = DocItemShowAdapter(context, bidDetail?.Shipments!!)
        binding.docRecycler.adapter =docAdapter



    }

    private fun setAdapterView1(context: Context?) {
        if(heavyFreightBidDetails?.PackageImages?.isNullOrEmpty() == true) {
            binding.defaultImage.visibility=View.VISIBLE
            binding.bidImages.visibility=View.GONE
        }else{
            binding.bidImages.visibility=View.VISIBLE
            binding.defaultImage.visibility=View.GONE
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.bidImages.layoutManager = layoutManager
            imageAdapter = ShowBidImageAdapter(
                context,
                heavyFreightBidDetails?.PackageImages!!,
                onItemClick = ::onItemClick
            )
            binding.bidImages.adapter = imageAdapter
        }

        if(heavyFreightBidDetails?.Dimensions?.get(0)?.Packaging ==""){
            binding.more.visibility=View.GONE
            binding.less.visibility=View.GONE
        }

        val layoutManager1 = GridLayoutManager(activity, 2)
        binding.docRecycler.layoutManager = layoutManager1
        (binding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        docItemHeavyFreightShowAdapter = CompletedHeavyFreightAdapter(
            context,
            heavyFreightBidDetails?.Dimensions!!,
            completedBidItem
        )
        binding.docRecycler.adapter =docItemHeavyFreightShowAdapter



    }

    fun onItemClick(imagePath:String){

    }

    override fun onClick(view: View?) {
        when (view?.id) {
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
        }
    }
}