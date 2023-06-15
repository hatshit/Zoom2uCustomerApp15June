package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers.BidOffersFragment
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_biddetails.BidDetailsFragment

class BidViewPagerAdapter(
    fm: FragmentManager,
    var bidDetails: BidDetailsResponse?,
    var heavyFreightBidDetails: HeavyFreightBidDetails?,
    var activeBidItem: ActiveBidListResponse?
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = BidDetailsFragment.newInstance(bidDetails,heavyFreightBidDetails,activeBidItem)
        } else if (position == 1) {
            fragment = BidOffersFragment.newInstance(bidDetails,heavyFreightBidDetails)
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