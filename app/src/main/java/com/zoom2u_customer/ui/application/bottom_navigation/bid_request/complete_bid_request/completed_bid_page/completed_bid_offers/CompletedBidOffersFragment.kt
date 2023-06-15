package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.completed_bid_offers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.zoom2u_customer.databinding.FragmentCompletedBidOffersBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_biddetails.BidDetailsFragment
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.CompletedDetailsResponse

class CompletedBidOffersFragment() : Fragment() {
    lateinit var binding: FragmentCompletedBidOffersBinding
    private var heavyFreightBidDetails: HeavyFreightBidDetails? =null
    var bidDetail: CompletedDetailsResponse? = null

    companion object {

        var bidDetail1: CompletedDetailsResponse? = null
        var heavyFreightBidDetails1: HeavyFreightBidDetails? =null
        fun newInstance(
            bidDetails: CompletedDetailsResponse?,
            heavyFreightBidDetails: HeavyFreightBidDetails?
        ): CompletedBidOffersFragment {
            this.bidDetail1 = bidDetails
            this.heavyFreightBidDetails1 = heavyFreightBidDetails
            return CompletedBidOffersFragment()
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompletedBidOffersBinding.inflate(inflater, container, false)
        this.bidDetail= bidDetail1
        this.heavyFreightBidDetails = heavyFreightBidDetails1
        if (container != null) {
            if(bidDetail!=null)
                setAdapterView(binding, container.context)
            else if(heavyFreightBidDetails!=null)
                setAdapterView1(binding,container.context)
        }

        return binding.root
    }

    fun setAdapterView(binding: FragmentCompletedBidOffersBinding, context: Context) {
        val layoutManager = GridLayoutManager(activity, 1)
        binding.activeBidOffersRecycler.layoutManager = layoutManager
        val adapter =
            CompletedBidOffersAdapter(context, bidDetail?.Offers?.toList()!!, onItemClick = ::onBidOfferSelected,false)
        binding.activeBidOffersRecycler.adapter = adapter

    }
    fun setAdapterView1(binding: FragmentCompletedBidOffersBinding, context: Context) {
        val layoutManager = GridLayoutManager(activity, 1)
        binding.activeBidOffersRecycler.layoutManager = layoutManager
        val adapter =
            CompletedBidOffersAdapter(context, heavyFreightBidDetails?.Offers?.toList()!!, onItemClick = ::onBidOfferSelected,true)
        binding.activeBidOffersRecycler.adapter = adapter

    }
    private fun onBidOfferSelected(offer:Offer) {

    }
}