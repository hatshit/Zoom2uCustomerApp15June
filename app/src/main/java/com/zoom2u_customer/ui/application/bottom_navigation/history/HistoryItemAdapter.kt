package com.zoom2u_customer.ui.application.bottom_navigation.history

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.zoom2u_customer.databinding.ItemDeliveryHistoryBinding
import com.zoom2u_customer.ui.application.bottom_navigation.history.history_details.DocItemListShowAdapter
import java.util.*

class HistoryItemAdapter(val context: Context, private val onItemClick:(HistoryResponse) -> Unit,private val onHoldClick:(HistoryResponse) -> Unit,
                         private val onApiCall:() ->Unit) :
    RecyclerView.Adapter<HistoryItemAdapter.BindingViewHolder>() {
    private var dataList1:MutableList<HistoryResponse> = ArrayList()
    private var lastApiCallPosition:Int=-1
    private var docAdapter: DocItemListShowAdapter?=null

    fun updateRecords(dataList: MutableList<HistoryResponse>) {

        dataList1.clear()
        this.dataList1.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataList1.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemDeliveryHistoryBinding =
            ItemDeliveryHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if(dataList1[position].type==1){
            if(dataList1[position].count>0) {
                holder.itemBinding.heading.visibility = View.VISIBLE
                holder.itemBinding.heading.text = "Ongoing(${dataList1[position].count})"
                holder.itemBinding.cl2.visibility = View.GONE
            }else{
                holder.itemBinding.cl2.visibility = View.GONE
            }
        }else if(dataList1[position].type==2){
            holder.itemBinding.heading.visibility= View.VISIBLE
            holder.itemBinding.heading.text = "Past"
            holder.itemBinding.cl2.visibility=View.GONE
        }else{
            /*space in below last item */
            if (position == dataList1.size - 1)
                holder.itemBinding.blankView.visibility = View.VISIBLE
            else
                holder.itemBinding.blankView.visibility = View.GONE

            val historyResponse: HistoryResponse = dataList1[position]
            holder.itemBinding.historyitem= historyResponse


            holder.itemBinding.onHoldBtn.setOnClickListener{
                if(dataList1[position].IsOnHold==true){
                    onHoldClick(historyResponse)
                }
            }


            holder.itemBinding.root.setOnClickListener {
              onItemClick(historyResponse)
            }

            if(position==dataList1.size-1) {
                if(lastApiCallPosition!=position){
                    lastApiCallPosition=position
                    onApiCall()
                }
            }

            if(dataList1[position].IsCancel==true &&dataList1[position].IsOnHold==false) {
                holder.itemBinding.price.text = "No Charge"
                holder.itemBinding.status.text = "Cancelled"
                holder.itemBinding.name.text = "-"
                holder.itemBinding.status.visibility=View.VISIBLE
                holder.itemBinding.onHoldBtn.visibility=View.GONE
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#ff0000"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
             }else if(dataList1[position].IsOnHold==true &&dataList1[position].IsCancel==false){
                holder.itemBinding.price.text = "$" + dataList1[position].Price.toString()
                holder.itemBinding.status.visibility=View.GONE
                holder.itemBinding.onHoldBtn.visibility=View.VISIBLE
                holder.itemBinding.name.text = "Awaiting confirmation"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#ff0000"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }else if(dataList1[position].IsOnHold==true &&dataList1[position].IsCancel==true){
                holder.itemBinding.price.text = "No Charge"
                holder.itemBinding.status.visibility=View.GONE
                holder.itemBinding.onHoldBtn.visibility=View.VISIBLE
                holder.itemBinding.name.text = "-"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#ff0000"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }
            else {
                holder.itemBinding.price.text = "$" + dataList1[position].Price.toString()
                holder.itemBinding.name.text = dataList1[position].CourierFirstName+" "+dataList1[position].CourierLastName
                setStatus(dataList1[position].Status,holder)
            }


        }


        if(dataList1[position].Shipments!=null){
                if (dataList1[position].Shipments?.size != 0) {
                    holder.itemBinding.ll1.visibility = View.GONE
                    holder.itemBinding.doc.visibility = View.VISIBLE
                    val layoutManager1 = GridLayoutManager(context, 2)
                    holder.itemBinding.docRecycler.layoutManager = layoutManager1
                    (holder.itemBinding.docRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                        false
                    docAdapter = DocItemListShowAdapter(context, dataList1[position].Shipments!!)
                    holder.itemBinding.docRecycler.adapter = docAdapter

                } else {
                    holder.itemBinding.ll1.visibility = View.VISIBLE
                    holder.itemBinding.doc.visibility = View.GONE
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun setStatus(status: String?, holder: HistoryItemAdapter.BindingViewHolder) {


        when (status) {
            "Accepted" -> {
                holder.itemBinding.status.text = "Allocated"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                holder.itemBinding.status.setTextColor(Color.BLACK)
            }
            "Picked up" -> {
                holder.itemBinding.status.text = "Picked up"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }
            "Dropped Off" -> {
                holder.itemBinding.status.text ="Delivered"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#76D750"))
                holder.itemBinding.status.setTextColor(Color.BLACK)
            }
            "On Route to Pickup" -> {
                holder.itemBinding.status.text ="On Route to-Pickup"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }
            "On Route to Dropoff" -> {
                holder.itemBinding.status.text ="On Route to-Dropoff"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }
            "Tried to deliver" -> {
                holder.itemBinding.status.text ="Tried to deliver"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#00A7E2"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }
            "Returned"  ->{
                holder.itemBinding.status.text ="Returned"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#ff0000"))
                holder.itemBinding.status.setTextColor(Color.WHITE)
            }
            else -> {
                holder.itemBinding.status.text ="Accepted"
                holder.itemBinding.status.setBackgroundColor(Color.parseColor("#FFD100"))
                holder.itemBinding.status.setTextColor(Color.BLACK)
                holder.itemBinding.name.text = "Awaiting confirmation"
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateItem(updatedHistoryItem: HistoryResponse?) {
        dataList1.forEachIndexed { index, pod ->
            if (pod.BookingRef == updatedHistoryItem?.BookingRef) {
                pod.apply {
                    IsCancel=updatedHistoryItem?.IsCancel
                }
                notifyItemChanged(index)

            }
        }
    }

    fun updateItem1(updatedHistoryItem: HistoryResponse?) {
        dataList1.forEachIndexed { index, pod ->
            if (pod.BookingRef == updatedHistoryItem?.BookingRef) {
                pod.apply {
                    IsOnHold=updatedHistoryItem?.IsOnHold
                }
                notifyItemChanged(index)

            }
        }
    }

    fun setCount() {
        lastApiCallPosition=-1
    }

    class BindingViewHolder(val itemBinding: ItemDeliveryHistoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}