package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.FragmentCompleteBidBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request.completed_bid_page.CompletedBidActivity
import com.zoom2u_customer.utility.AppUtility

class CompleteBidFragment : Fragment() {
    lateinit var binding: FragmentCompleteBidBinding
    lateinit var viewModel: CompletedBidListViewModel
    private var  repository: CompletedBidListRepository? = null
    private var adapter:CompletedItemAdapter? = null
    private var currentPage: Int = 1
    private val listData: MutableList<CompletedBidListResponse> = ArrayList()
    private var isListNotEmpty:Boolean=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompleteBidBinding.inflate(inflater, container, false)

        if (container != null) {
            setAdapterView(binding,container.context)
        }
        viewModel = ViewModelProvider(this).get(CompletedBidListViewModel::class.java)
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = CompletedBidListRepository(serviceApi, container?.context)
        viewModel.repository = repository
        currentPage = 1
        viewModel.getCompletedBidList(currentPage)

        binding.shimmerLayout.startShimmer()

        binding.swipeRefresh.setOnRefreshListener(OnRefreshListener {
            listData.clear()
            adapter?.setCount()
            viewModel.getCompletedBidList(1)
        })

        viewModel.getCompletedBidListSuccess().observe(viewLifecycleOwner) {
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
                    val listType = object : TypeToken<List<CompletedBidListResponse?>?>() {}.type
                    val list: List<CompletedBidListResponse> =
                        Gson().fromJson(convert, listType)
                    if (list.isNotEmpty()) {
                        isListNotEmpty=true
                        updateRecord(list.toMutableList())
                        binding.noCompletedBidText.visibility = View.GONE
                    } else {
                        if(isListNotEmpty)
                            binding.noCompletedBidText.visibility = View.GONE
                        else
                            binding.noCompletedBidText.visibility = View.VISIBLE
                    }
                }
            }
        }


        return binding.root

    }

    private fun updateRecord(listWithOutFreight: MutableList<CompletedBidListResponse>) {
        if (listWithOutFreight.size > 0)
            listData.addAll(listWithOutFreight)
        else {
            listData.clear()
            listData.addAll(listWithOutFreight)
        }

        adapter?.updateRecords(listData)
    }



    private fun setAdapterView(binding: FragmentCompleteBidBinding, context: Context) {
        val layoutManager = GridLayoutManager(activity, 1)
        binding.completedBidRecycler.layoutManager = layoutManager
        adapter = CompletedItemAdapter(context,
            onItemClick = ::onItemClick,
            onApiCall = ::onApiCall)
        binding.completedBidRecycler.adapter=adapter

    }

    private fun onApiCall() {
        currentPage++
        AppUtility.progressBarShow(activity)
        viewModel.getCompletedBidList(currentPage)
    }
    private fun onItemClick(completedBidItem: CompletedBidListResponse){
        /**commenting for completed booking not opening details as per the IOS working*/
     /*   val intent = Intent(activity, CompletedBidActivity::class.java)
        intent.putExtra("CompletedBidItem", completedBidItem)
        startActivity(intent)*/
    }

}