package com.zoom2u_customer.ui.application.bottom_navigation.home.booking_confirmation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.databinding.ItemBookingConfirmationBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon

class BookingPackageAdapter(
    val context: Context,
    private val dataList: List<Icon>
    ) : RecyclerView.Adapter<BookingPackageAdapter.BindingViewHolder>() {

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemBookingConfirmationBinding =
            ItemBookingConfirmationBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val icon: Icon = dataList[position]
        holder.itemBinding.icon = icon

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class BindingViewHolder(val itemBinding: ItemBookingConfirmationBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}