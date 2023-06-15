package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation.interstate_booking

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.databinding.ItemDangerBinding

class DangerIconAdapter(
    val context: Context,
    private var dataList: List<DangerIcon>,

) :
    RecyclerView.Adapter<DangerIconAdapter.BindingViewHolder>() {

    fun updateRecords(dataList: List<DangerIcon>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemDangerBinding =
            ItemDangerBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val dangerIcon: DangerIcon = dataList[position]
        holder.itemBinding.dangerIcon=dangerIcon

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class BindingViewHolder(val itemBinding: ItemDangerBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}