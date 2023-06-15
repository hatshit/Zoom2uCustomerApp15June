package com.zoom2u_customer.ui.application.bottom_navigation.bid_request


import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentPagerAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidFragment
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.CompleteBidFragment


class ViewPagerAdapter (fm: androidx.fragment.app.FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        if (position == 0) {
            fragment = ActiveBidFragment()
        } else if (position == 1) {
            fragment = CompleteBidFragment()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String? = null
        if (position == 0) {
            title = "Active"
        } else if (position == 1) {
            title = "Complete"
        }
        return title
    }
}