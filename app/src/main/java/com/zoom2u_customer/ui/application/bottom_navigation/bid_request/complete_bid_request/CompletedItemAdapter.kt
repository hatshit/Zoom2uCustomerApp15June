package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.complete_bid_request

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ItemCompletedBidBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveItemAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.history.history_details.DocItemListShowAdapter
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.model.ShipmentsClass
import com.zoom2u_customer.utility.AppUtility
import java.util.*


class CompletedItemAdapter (val context: Context, private val onItemClick:(CompletedBidListResponse) -> Unit, private val onApiCall:() ->Unit) :
    RecyclerView.Adapter<CompletedItemAdapter.BindingViewHolder>() {
    private var dataList:MutableList<CompletedBidListResponse> = ArrayList()
    private var lastApiCallPosition:Int=-1
    private var docAdapter: DocItemListShowAdapter?=null
    override fun getItemCount(): Int {
        return dataList.size
    }
    fun updateRecords(dataList1: List<CompletedBidListResponse>) {
        dataList.clear()
        this.dataList.addAll(dataList1)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemCompletedBidBinding =
            ItemCompletedBidBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        if (position == dataList.size-1)
            holder.itemBinding.blankView.visibility = View.VISIBLE
        else
            holder.itemBinding.blankView.visibility = View.GONE
        val completedBidItem: CompletedBidListResponse = dataList[position]
        holder.itemBinding.completedbid= completedBidItem
        holder.itemBinding.root.setOnClickListener {
            /*if(dataList[position].ItemType=="Freight"||dataList[position].ItemCategory=="XL"){
                DialogActivity.alertDialogSingleButton(context, "Alert!", "Currently not showing Freight and Extra Large item information try in portal.")
            }else*/
                onItemClick(completedBidItem)
        }
        if(position==dataList.size-1) {
            if(lastApiCallPosition!=position){
                lastApiCallPosition=position
                onApiCall()
            }
        }


        val pickDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].PickupDateTime)
        val pickUpDateTimeSplit: Array<String>? = pickDateTime?.split(" ")?.toTypedArray()
        holder.itemBinding.pickTime.text =
            pickUpDateTimeSplit?.get(1) + " " + pickUpDateTimeSplit?.get(2) + " | " + pickUpDateTimeSplit?.get(0)

        val dropDateTime = AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].DropDateTime)
        val dropUpDateTimeSplit: Array<String>? = dropDateTime?.split(" ")?.toTypedArray()
        holder.itemBinding.dropTime.text =
            dropUpDateTimeSplit?.get(1) + " " + dropUpDateTimeSplit?.get(2) + " | " + dropUpDateTimeSplit?.get(0)

        holder.itemBinding.ref.text= "Ref #:${dataList[position].QuoteRef.toString()}"

        setItemType(holder,dataList[position])

    }

    fun setItemType(holder: BindingViewHolder, data: CompletedBidListResponse){
        if(data.ItemType=="Freight") {
            when (data.ItemCategory) {
                "0" ->{
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "General Van Shipments"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_general_van_shipments)
                }

                "2" -> {
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "Building Materials"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_building_materials)
                }
                "3"
                -> {
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "General Truck Shipments"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_general_truck_shipments)
                }
                "4" -> {
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "Pallets"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_pallets)
                }
                "5" -> {
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "Marchinery"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_machinery)
                }
                "6" -> {
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "Vehicles"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_vehicles)
                }
                "7" ->{
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "Container"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_container)
                }
                "8" ->{
                    holder.itemBinding.doc.visibility=View.GONE
                    holder.itemBinding.ll1.visibility=View.VISIBLE
                    holder.itemBinding.docTxt.text = "Full Truck Load"
                    holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_full_truck_load)
                }

                else ->{
                    holder.itemBinding.doc.visibility=View.VISIBLE
                    holder.itemBinding.ll1.visibility=View.GONE
                    if(data.Shipments!=null) {
                        /**remove extra space for heavy freight case*/
                        if(data.Shipments?.get(0)?.Category =="Custom"&&data.Shipments?.get(0)?.Packaging==""){
                            val heavyFreight: MutableList<ShipmentsClass> = ArrayList()
                            heavyFreight.add( data.Shipments!![0])
                            val layoutManager1 = GridLayoutManager(context, 2)
                            holder.itemBinding.docRecycler.layoutManager = layoutManager1
                            (holder.itemBinding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                            docAdapter = DocItemListShowAdapter(context,heavyFreight)
                            holder.itemBinding.docRecycler.adapter = docAdapter
                        }
                        else{
                            val layoutManager1 = GridLayoutManager(context, 2)
                            holder.itemBinding.docRecycler.layoutManager = layoutManager1
                            (holder.itemBinding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                                false
                            docAdapter = DocItemListShowAdapter(context, data.Shipments!!)
                            holder.itemBinding.docRecycler.adapter = docAdapter
                        }
                    }
                }
            }

        }
        else if(data.ItemCategory=="XL") {
            holder.itemBinding.doc.visibility=View.GONE
            holder.itemBinding.ll1.visibility=View.VISIBLE
            holder.itemBinding.docTxt.text = "Large items"
            holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_machinery)
        }else if(data.ItemCategory!="XL"&&data.ItemType!="Freight")  {
            holder.itemBinding.doc.visibility=View.VISIBLE
            holder.itemBinding.ll1.visibility=View.GONE
            if(data.Shipments!=null) {
                val layoutManager1 = GridLayoutManager(context, 2)
                holder.itemBinding.docRecycler.layoutManager = layoutManager1
                (holder.itemBinding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                docAdapter = DocItemListShowAdapter(context, data.Shipments!!)
                holder.itemBinding.docRecycler.adapter = docAdapter

            }
        }



    }



    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setCount() {
        lastApiCallPosition=-1
    }


    class BindingViewHolder(val itemBinding: ItemCompletedBidBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}