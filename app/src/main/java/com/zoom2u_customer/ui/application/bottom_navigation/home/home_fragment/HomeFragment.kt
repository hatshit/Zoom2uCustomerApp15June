package com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment


import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.FragmentHomeBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.pickup_details.PickUpDetailsActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.map_page.MapActivity
import com.zoom2u_customer.ui.application.chat.ChatActivity
import com.zoom2u_customer.ui.application.chat.ChatListRepository
import com.zoom2u_customer.ui.application.chat.LoadChatBookingArray
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity


class HomeFragment : Fragment(), View.OnClickListener {
    lateinit var binding: FragmentHomeBinding
    private var itemList: MutableList<Icon> = ArrayList()
    private var largeItem: Icon?=null
    lateinit var adapter: IconAdapter
    var repositoryChat: ChatListRepository? = null
    private var isLargeItemSelected:Boolean=false
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("message")) {
                "from_bid_to_home"->{
                    setDefaultData()
                    setLargeItemDefaultData()
                    binding.blankView.visibility = View.VISIBLE
                    binding.getQuoteBtn.visibility = View.GONE
                }
                "from_active_bid" -> {
                    LoadChatBookingArray.showNotifyIconForUnreadChat(binding.count)
                }
                "from_booking_confirmation" -> {
                    setDefaultData()
                    setLargeItemDefaultData()
                    binding.blankView.visibility = View.VISIBLE
                    binding.getQuoteBtn.visibility = View.GONE
                }
                "form_on_hold_page" -> {
                    setDefaultData()
                    setLargeItemDefaultData()
                    binding.blankView.visibility = View.VISIBLE
                    binding.getQuoteBtn.visibility = View.GONE
                }
            }
        }
    }



    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcastReceiver, IntentFilter("home_page"))

        if (container != null) {
            setAdapterView(binding, container.context)
        }
        binding.getQuoteBtn.setOnClickListener(this)
        binding.chatBtn.setOnClickListener(this)
        (binding.iconView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        if (!AppPreference.getSharedPrefInstance().getProfileData()?.FirstName.isNullOrBlank())
            binding.nameHeader.text = "Hi " + AppUtility.upperCaseFirst(
                AppPreference.getSharedPrefInstance().getProfileData()?.FirstName.toString()
            )
        else
            binding.nameHeader.text = "Hi " + AppUtility.upperCaseFirst(
                AppPreference.getSharedPrefInstance().getLoginResponse()?.firstName.toString()
            )

        if (itemList.size > 0) {
            binding.getQuoteBtn.visibility = View.VISIBLE
        } else
            binding.getQuoteBtn.visibility = View.GONE

        return binding.root
    }


    fun setAdapterView(binding: FragmentHomeBinding, context: Context) {
        val layoutManager = GridLayoutManager(activity, 2)
        binding.iconView.layoutManager = layoutManager
        adapter = IconAdapter(context, IconDataProvider.iconList, isItemClick = ::isItemClick, isLargeItemClick = ::isLargeItemClick)
        binding.iconView.adapter = adapter


    }

    private fun isLargeItemClick() {
        binding.iconView.post {
            binding.iconView.smoothScrollToPosition(10)
           /* val resId: Int = R.anim.layout_animation
            val animation: LayoutAnimationController =
                AnimationUtils.loadLayoutAnimation(context, resId)
            binding.iconView.layoutAnimation=animation*/
        }
    }

    private fun isItemClick(dataList: MutableList<Icon>) {
        var i = 0
        for (item in dataList) {
            if(item.Value!=25 && item.isSelected){
                binding.blankView.visibility = View.GONE
                binding.getQuoteBtn.visibility = View.VISIBLE
                largeItem=item
                isLargeItemSelected=true
            }
            else if (item.quantity > 0) {
                isLargeItemSelected=false
                binding.blankView.visibility = View.GONE
                binding.getQuoteBtn.visibility = View.VISIBLE
            } else if (item.quantity == 0) {
                i++
               if (i == 16) {
                    binding.blankView.visibility = View.VISIBLE
                    binding.getQuoteBtn.visibility = View.GONE
                }
            }else{
                isLargeItemSelected=false
                binding.blankView.visibility = View.VISIBLE
                binding.getQuoteBtn.visibility = View.GONE
            }
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.get_quote_btn -> {
                binding.getQuoteBtn.isClickable = false
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.getQuoteBtn.isClickable = true

                }, 1000)
                setItemData()
                when {
                    itemList.size > 0 -> {
                        val intent = Intent(activity, MapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putParcelableArrayListExtra("icon_data", ArrayList(itemList.toList()))
                        startActivityForResult(intent, 11)
                    }
                    isLargeItemSelected -> {
                        val intent = Intent(activity, PickUpDetailsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putExtra("LargeItem", largeItem)
                        startActivityForResult(intent, 12)
                    }
                    else -> {
                        DialogActivity.alertDialogOkCallbackWithoutHeader(
                            activity,
                            "You’ll need to select what you’re trying to send first!" +
                                    "\n" +
                                    "Please select type of parcels you want to send and we’ll sort out the rest.",
                            onItemClick = ::onItemClick
                        )
                    }
                }

            }
            R.id.chat_btn -> {
                val intent = Intent(activity, ChatActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun onItemClick() {
        binding.getQuoteBtn.isClickable = true
    }

    private fun setItemData() {
        for (item in IconDataProvider.iconList) {
            if (item.quantity > 0)
                itemList.add(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 11) {
            binding.blankView.visibility = View.VISIBLE
            binding.getQuoteBtn.visibility=View.GONE
            setDefaultData()
        }else if(requestCode === 12) {
            binding.blankView.visibility = View.VISIBLE
            binding.getQuoteBtn.visibility=View.GONE
            setLargeItemDefaultData()
        }
    }

    private fun setLargeItemDefaultData() {
        adapter.isComeInHomePage(true)
        adapter.isHeavyFreight(false)
    }

    private fun setDefaultData() {
        for (item in IconDataProvider.iconList) {
            item.quantity = 0
            item.isSelected=false
            adapter.updateItem(item)
        }
        itemList.clear()
    }

    override fun onResume() {
        super.onResume()
        binding.blankView.visibility = View.VISIBLE
        binding.getQuoteBtn.visibility=View.GONE
        setDefaultData()
        setLargeItemDefaultData()
        LoadChatBookingArray.showNotifyIconForUnreadChat(binding.count)

    }



    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

}