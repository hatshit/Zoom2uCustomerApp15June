package com.zoom2u_customer.ui.application.bottom_navigation.profile.change_price

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.databinding.ItemChnagePriceBinding

class PriceChangeAdapter(val context: Context, private val dataList: List<PriceChange>) :
    RecyclerView.Adapter<PriceChangeAdapter.BindingViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemChnagePriceBinding =
            ItemChnagePriceBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val priceChange: PriceChange = dataList[position]
        holder.itemBinding.priceChange= priceChange
        /*holder.itemBinding.root.setOnClickListener {
            onItemClick(historyItem)
        }*/

    }


    class BindingViewHolder(val itemBinding: ItemChnagePriceBinding) :
        RecyclerView.ViewHolder(itemBinding.root)



}