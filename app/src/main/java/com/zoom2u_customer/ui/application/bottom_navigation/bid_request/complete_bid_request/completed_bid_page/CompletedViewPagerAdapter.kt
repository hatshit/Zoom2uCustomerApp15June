package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.CompletedBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.completed_bid_details.CompletedDetailsFragment
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.completed_bid_offers.CompletedBidOffersFragment

class CompletedViewPagerAdapter(
    fm: FragmentManager,
    var bidDetails: CompletedDetailsResponse?,
    var heavyFreightBidDetails: HeavyFreightBidDetails?,
    var completedBidItem: CompletedBidListResponse?
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = CompletedDetailsFragment.newInstance(bidDetails,heavyFreightBidDetails,completedBidItem)
        } else if (position == 1) {
            fragment = CompletedBidOffersFragment.newInstance(bidDetails,heavyFreightBidDetails)
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Bid Details"
        } else if (position == 1) {
            title = if(bidDetails!=null)
                "Offers(${bidDetails?.Offers?.size})"
            else
                "Offers(${heavyFreightBidDetails?.Offers?.size})"
        }
        return title
    }
}