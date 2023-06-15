package com.zoom2u_customer.ui.application.bottom_navigation.bid_request

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.tabs.TabLayout
import com.zoom2u_customer.databinding.FragmentBidRequestBinding
import com.zoom2u_customer.ui.application.chat.ChatActivity
import com.zoom2u_customer.ui.application.chat.LoadChatBookingArray


class BidRequestFragment : Fragment() {
    lateinit var binding: FragmentBidRequestBinding
    private lateinit var viewpageradapter: ViewPagerAdapter


    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("message")) {
                "from_base_to_bid"->{
                    LoadChatBookingArray.showNotifyIconForUnreadChat(binding.count)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBidRequestBinding.inflate(inflater, container, false)

        viewpageradapter = ViewPagerAdapter(childFragmentManager)

        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("bid_refresh"))
        binding.pager.adapter = viewpageradapter
        binding.tabLayout.setupWithViewPager(binding.pager)


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.pager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


        binding.chatBtn.setOnClickListener() {
            val intent = Intent(activity, ChatActivity::class.java)
            startActivity(intent)
        }

        //val loadChatBookingArray = LoadChatBookingArray(activity as Context)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
         LoadChatBookingArray.showNotifyIconForUnreadChat(binding.count)

    }
}