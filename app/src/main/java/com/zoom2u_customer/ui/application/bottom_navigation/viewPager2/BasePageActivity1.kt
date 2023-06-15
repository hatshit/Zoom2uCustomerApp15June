package com.zoom2u_customer.ui.application.bottom_navigation.viewPager2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityBasePage1Binding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.BidRequestFragment
import com.zoom2u_customer.ui.application.bottom_navigation.history.HistoryFragment
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.HomeFragment
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileFragment

class BasePageActivity1 : AppCompatActivity() {
    lateinit var binding: ActivityBasePage1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (R.layout.activity_main)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_base_page1)


        binding.vp.apply {
            adapter = ViewPagerAdapter(this@BasePageActivity1)
            registerOnPageChangeCallback (object :
                            ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(position: Int) {
                                super.onPageSelected(position)
                              binding.bn.selectedItemId = when (position) {
                                    0 -> R.id.navigation_home
                                    1 -> R.id.navigation_booking
                                    2 -> R.id.navigation_history
                                    else -> R.id.navigation_profile
                                }
                            }
                        })
                }
       binding.bn.setOnNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.navigation_home -> binding.vp.currentItem = 0
                        R.id.navigation_booking -> binding.vp.currentItem = 1
                        R.id.navigation_history -> binding.vp.currentItem =2
                        else ->binding. vp.currentItem = 3
                    }
            true
                }
            }

    inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
                override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> HomeFragment()
                            1 -> BidRequestFragment()
                            2 -> HistoryFragment()
                            else -> ProfileFragment()
                        }
                    }
            }


}