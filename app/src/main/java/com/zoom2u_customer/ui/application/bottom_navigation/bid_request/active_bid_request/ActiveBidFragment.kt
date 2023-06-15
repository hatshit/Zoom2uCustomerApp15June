package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.FragmentActiveBidBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.ActiveBidActivity

import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import java.util.*
import kotlin.collections.ArrayList


class ActiveBidFragment : Fragment() {
    private var selectForCancel: Int? = null
    private var selectForCancelItemType: String? = null
    lateinit var binding: FragmentActiveBidBinding
    lateinit var viewModel: ActiveBidListViewModel
    private var repository: ActiveBidListRepository? = null
    private var adapter: ActiveItemAdapter? = null
    private var currentPage: Int = 1
    val listData: MutableList<ActiveBidListResponse> = ArrayList()
    private var isListNotEmpty:Boolean=false
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            listData.clear()
            adapter?.setCount()
            viewModel.getActiveBidList(1)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentActiveBidBinding.inflate(inflater, container, false)

        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadcastReceiver, IntentFilter("bid_refresh1"))
        if (container != null) {
            setAdapterView(binding, container.context)
        }
        viewModel = ViewModelProvider(this).get(ActiveBidListViewModel::class.java)
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = ActiveBidListRepository(serviceApi, container?.context)
        viewModel.repository = repository
        currentPage = 1
        viewModel.getActiveBidList(currentPage)

        binding.shimmerLayout.startShimmer()
        binding.noActiveBidText.visibility = View.GONE

        binding.swipeRefresh.setOnRefreshListener(OnRefreshListener {
            listData.clear()
            adapter?.setCount()
            viewModel.getActiveBidList(1)
        })

        viewModel.getActiveBidListSuccess().observe(viewLifecycleOwner) {
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
                    val listType = object : TypeToken<List<ActiveBidListResponse?>?>() {}.type
                    val list: List<ActiveBidListResponse> =
                        Gson().fromJson(convert, listType)
                    if (list.isNotEmpty()) {
                        isListNotEmpty=true
                        updateRecord(list.toMutableList())
                        binding.noActiveBidText.visibility = View.GONE
                    } else {
                        updateRecord(Collections.emptyList())
                        if(isListNotEmpty)
                            binding.noActiveBidText.visibility = View.VISIBLE
                    }
                }
            }else{
                binding.noActiveBidText.visibility = View.VISIBLE
            }

        }

        return binding.root

    }

    private fun updateRecord(listWithOutFreight: MutableList<ActiveBidListResponse>) {
        if (listWithOutFreight.size > 0){
            listData.addAll(listWithOutFreight)
        }
        else {
            listData.clear()
            listData.addAll(listWithOutFreight)
        }

        adapter?.updateRecords(listData)
    }

    private fun setAdapterView(binding: FragmentActiveBidBinding, context: Context) {
        val layoutManager = GridLayoutManager(activity, 1)
        binding.activeBidRecycler.layoutManager = layoutManager
        adapter = ActiveItemAdapter(
            context,
            onItemClick = ::onItemClick,
            onApiCall = ::onApiCall,
            onCancelClick = ::onCancelClick
        )
        binding.activeBidRecycler.adapter = adapter

    }

    private fun onApiCall() {
        currentPage++
        if (listData.size > 9) {
            AppUtility.progressBarShow(activity)
            viewModel.getActiveBidList(currentPage)
        }
    }

    private fun onItemClick(activeBidItem: ActiveBidListResponse, pos: Int) {
        val intent = Intent(activity, ActiveBidActivity::class.java)
        intent.putExtra("ActiveBidItem", activeBidItem)
        intent.putExtra("pos", pos.toString())
        startActivityForResult(intent, 3)
    }

    private fun onCancelClick(Id: Int, itemType: String) {
        selectForCancel = Id
        selectForCancelItemType = itemType
        DialogActivity.logoutDialog(
            activity,
            "Confirm!",
            "Are you sure you want to cancel your request?",
            "Yes", "No",
            onCancelClick = ::onNoClick,
            onOkClick = ::onYesClick
        )
    }

    fun onNoClick() {}
    private fun onYesClick() {
        /* if (selectForCancelItemType == "Freight")
             viewModel.getHeavyBidCancel(selectForCancel)
         else if (selectForCancelItemType == "ExtraLargeItem")
             viewModel.getBidCancel(selectForCancel)*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 3) {
                val pos: Int = data.getStringExtra("pos")!!.toInt()
                adapter?.removeItem(pos)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

}