package com.zoom2u_customer.ui.application.bottom_navigation.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.FragmentHistoryBinding
import com.zoom2u_customer.ui.application.bottom_navigation.history.history_details.HistoryDetailsActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.order_confirm_hold.OnHoldActivity
import com.zoom2u_customer.ui.application.chat.ChatActivity
import com.zoom2u_customer.ui.application.chat.LoadChatBookingArray
import com.zoom2u_customer.utility.AppUtility


class HistoryFragment : Fragment() {
    lateinit var viewModel: HistoryViewModel
    private var repository: HistoryRepository? = null
    lateinit var binding: FragmentHistoryBinding
    private var adapter: HistoryItemAdapter? = null
    private var currentPage: Int = 1
    private var mergeHistoryList: MutableList<HistoryResponse> = ArrayList()
    private var isListNotEmpty:Boolean=false
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("message")) {
                "from_base_to_history"->{
                    LoadChatBookingArray.showNotifyIconForUnreadChat(binding.count)
                }
                "history_list_refresh"->{
                    mergeHistoryList.clear()
                    adapter?.setCount()
                    viewModel.getHistory(1)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("history"))

        if (container != null) {
            setAdapterView(binding, container.context)
        }

        binding.shimmerLayout.startShimmer()

        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        val serviceApi: ServiceApi = getServices()
        repository = HistoryRepository(serviceApi, container?.context)
        viewModel.repository = repository
        currentPage = 1
        viewModel.getHistory(currentPage)
        if(mergeHistoryList.size>0)
            mergeHistoryList.clear()

        viewModel.getHistoryList().observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                if (it == "false") {
                    AppUtility.progressBarDissMiss()
                    binding.swipeRefresh.isRefreshing = false
                } else {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility=View.GONE
                    AppUtility.progressBarDissMiss()
                    binding.swipeRefresh.isRefreshing = false
                    val convertedObject: JsonObject = Gson().fromJson(it, JsonObject::class.java)
                    val convert =
                        Gson().toJson(convertedObject.get("data")?.asJsonArray)
                    val listType = object : TypeToken<List<HistoryResponse?>?>() {}.type
                    val list: List<HistoryResponse> =
                        Gson().fromJson(convert, listType)

                    if (list.isNotEmpty()) {


                        val onGoingList: MutableList<HistoryResponse> = ArrayList()
                        val pastList: MutableList<HistoryResponse> = ArrayList()

                        /**count ongoing*/
                        for (item in list) {
                            if (System.currentTimeMillis() < AppUtility.getDateTime(item.DropDateTime).time) {
                                onGoingList.add(item)
                            } else {
                                pastList.add(item)
                            }
                        }
                        if (mergeHistoryList.size > 0) {
                            /**when pagination work*/
                            mergeHistoryList.addAll(onGoingList)
                            mergeHistoryList.addAll(pastList)

                        } else {
                            /**when first time and swipe refresh activity lunch*/
                            mergeHistoryList.clear()
                            mergeHistoryList.add(HistoryResponse().apply {
                                count = onGoingList.size
                                type = 1
                            })
                            mergeHistoryList.addAll(onGoingList)

                            mergeHistoryList.add(HistoryResponse().apply {
                                count = pastList.size
                                type = 2
                            })
                            mergeHistoryList.addAll(pastList)
                        }
                        isListNotEmpty=true
                        adapter?.updateRecords(mergeHistoryList)
                        binding.noHistoryText.visibility = View.GONE
                    } else
                        if(isListNotEmpty)
                            binding.noHistoryText.visibility = View.GONE
                        else
                            binding.noHistoryText.visibility = View.VISIBLE
                }
            }
            binding.chatBtn.setOnClickListener(){
                val intent = Intent(activity, ChatActivity::class.java)
                startActivity(intent)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            mergeHistoryList.clear()
            adapter?.setCount()
            viewModel.getHistory(1)
        }


        LoadChatBookingArray.showNotifyIconForUnreadChat(binding.count)
        return binding.root

    }

    fun setAdapterView(binding: FragmentHistoryBinding, context: Context) {
        val layoutManager = GridLayoutManager(activity, 1)
        binding.deliveryHistoryRecycler.layoutManager = layoutManager
        adapter = HistoryItemAdapter(
            context,
            onItemClick = ::onItemClick,
            onHoldClick = ::onHoldClick,
            onApiCall = ::onApiCall
        )
        binding.deliveryHistoryRecycler.adapter = adapter

    }

    private fun onApiCall() {
        currentPage++
        AppUtility.progressBarShow(activity)
        viewModel.getHistory(currentPage)
    }

    private fun onItemClick(historyResponse: HistoryResponse) {
        val intent = Intent(activity, HistoryDetailsActivity::class.java)
        intent.putExtra("HistoryItem", historyResponse)
        intent.putParcelableArrayListExtra("mergeHistoryList", ArrayList(mergeHistoryList.toList()))
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivityForResult(intent,85)
    }

    private fun onHoldClick(historyResponse: HistoryResponse) {
        val intentOnHold = Intent(context, OnHoldActivity::class.java)
        intentOnHold.putExtra("historyResponse", historyResponse)
        intentOnHold.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivityForResult(intentOnHold,85)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== 85 ) {
            if (data?.hasExtra("historyItem") == true) {
                val updatedHistoryItem: HistoryResponse? = data.getParcelableExtra<HistoryResponse>("historyItem")
                adapter?.updateItem(updatedHistoryItem)
            }else if(data?.hasExtra("historyItem1") == true){
                val updatedHistoryItem: HistoryResponse? = data.getParcelableExtra<HistoryResponse>("historyItem1")
                adapter?.updateItem1(updatedHistoryItem)
            }
        }
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

