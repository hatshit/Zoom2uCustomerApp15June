package com.zoom2u_customer.ui.application.bottom_navigation.base_page

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.zoom2u_customer.R
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.BidRequestFragment
import com.zoom2u_customer.ui.application.bottom_navigation.history.HistoryFragment
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.HomeFragment
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileFragment

enum class MainScreen(@IdRes val menuItemId: Int,
                      @StringRes val titleStringId: Int,
                      val fragment: Fragment
) {
    LOGS(R.id.navigation_home, R.string.home, HomeFragment()),
    PROGRESS(R.id.navigation_booking,  R.string.booking, BidRequestFragment() ),
    PROFILE(R.id.navigation_history,  R.string.history, HistoryFragment()),
    WORK(R.id.navigation_profile,  R.string.profile, ProfileFragment())
}

fun getMainScreenForMenuItem(menuItemId: Int): MainScreen? {
    for (mainScreen in MainScreen.values()) {
        if (mainScreen.menuItemId == menuItemId) {
            return mainScreen
        }
    }
    return null
}