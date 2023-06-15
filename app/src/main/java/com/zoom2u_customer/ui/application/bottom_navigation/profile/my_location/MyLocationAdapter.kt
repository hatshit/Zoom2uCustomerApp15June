package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.databinding.ItemMyLocationBinding
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq

class MyLocationAdapter(
    val context: Context,
    private var dataList: List<MyLocationResAndEditLocationReq>,
    private val onItemClick: (MyLocationResAndEditLocationReq) -> Unit
) :
    RecyclerView.Adapter<MyLocationAdapter.BindingViewHolder>() {

    fun updateRecords(dataList: List<MyLocationResAndEditLocationReq>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemMyLocationBinding =
            ItemMyLocationBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val myLocation: MyLocationResAndEditLocationReq = dataList[position]
        holder.itemBinding.myLocation = myLocation
        holder.itemBinding.editLocation.setOnClickListener {
            onItemClick(myLocation)
        }

        if (myLocation.DefaultDropoff == true)
            holder.itemBinding.defaultDropTxt.visibility = View.VISIBLE
        else
            holder.itemBinding.defaultDropTxt.visibility = View.GONE

        if (myLocation.DefaultPickup == true)
            holder.itemBinding.defaultPickTxt.visibility = View.VISIBLE
        else
            holder.itemBinding.defaultPickTxt.visibility = View.GONE
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class BindingViewHolder(val itemBinding: ItemMyLocationBinding) :
        RecyclerView.ViewHolder(itemBinding.root)
}