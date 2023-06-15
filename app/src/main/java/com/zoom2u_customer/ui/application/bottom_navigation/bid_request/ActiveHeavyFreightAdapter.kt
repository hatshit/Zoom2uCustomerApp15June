package com.zoom2u_customer.ui.application.bottom_navigation.bid_request

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ItemDocShowBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.HeavyFreightBidDetails

class ActiveHeavyFreightAdapter(
    val context: Context?,
    private val dataList: MutableList<HeavyFreightBidDetails.DimensionsClass>,
    val activeBidItem: ActiveBidListResponse?,
) : RecyclerView.Adapter<ActiveHeavyFreightAdapter.BindingViewHolder>() {

    private var isMoreEnable:Boolean?=null
    override fun getItemCount(): Int {
        return if(dataList.size>2){
            if(isMoreEnable == true)
                dataList.size
            else
                2
        }
        else
            dataList.size

    }

    fun isMoreEnable(isMoreEnable:Boolean){
        this.isMoreEnable=isMoreEnable
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemDocShowBinding =
            ItemDocShowBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        holder.itemBinding.count.text= "${dataList[position].Quantity.toString()}x"
        when (dataList[position].Packaging) {
            "Documents" -> {
                holder.itemBinding.docTxt.text="Documents"
                holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_documents_low)
            }
            "Bag"
            -> {
                holder.itemBinding.docTxt.text="Satchel,laptops"
                holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_satchelandlaptops_low)
            }
            "Box" -> {
                holder.itemBinding.docTxt.text="Small box"
                holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_smallbox_low)
            }
            "Flowers" -> {
                holder.itemBinding.docTxt.text="Cakes, flowers,delicates"
                holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_cakesflowersdelicates_low)
            }
            "Large" -> {
                holder.itemBinding.docTxt.text="Large box"
                holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_largebox_low)
            }
            "XL" ->{
                holder.itemBinding.count.visibility= View.GONE
                holder.itemBinding.docTxt.text="Large items"
                holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_large_items)
            }
            else -> {
                holder.itemBinding.count.visibility = View.GONE
                if (activeBidItem?.ItemType == "Freight") {
                   if(position==0) {
                       when (activeBidItem.ItemCategory) {
                           "0" -> {
                               holder.itemBinding.docTxt.text = "General Van Shipments"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_general_van_shipments)
                           }

                           "2" -> {
                               holder.itemBinding.docTxt.text = "Building Materials"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_building_materials)
                           }
                           "3"
                           -> {
                               holder.itemBinding.docTxt.text = "General Truck Shipments"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_general_truck_shipments)
                           }
                           "4" -> {
                               holder.itemBinding.docTxt.text = "Pallets"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_pallets)
                           }
                           "5" -> {
                               holder.itemBinding.docTxt.text = "Marchinery"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_machinery)
                           }
                           "6" -> {
                               holder.itemBinding.docTxt.text = "Vehicles"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_vehicles)
                           }
                           "7" -> {
                               holder.itemBinding.docTxt.text = "Container"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_container)
                           }
                           "8" -> {
                               holder.itemBinding.docTxt.text = "Full Truck Load"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_full_truck_load)
                           }
                           else ->{
                               holder.itemBinding.docTxt.text = "Heavy Freight"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_machinery)
                           }
                          /* "10" -> {
                               holder.itemBinding.docTxt.text = "Documents"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_documents_low)
                           }
                           "11" -> {
                               holder.itemBinding.docTxt.text = "Satchel,laptops"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_satchelandlaptops_low)
                           }
                           "12" -> {
                               holder.itemBinding.docTxt.text = "Small box"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_smallbox_low)
                           }
                           "13" -> {
                               holder.itemBinding.docTxt.text = "Cakes, flowers,delicates"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_cakesflowersdelicates_low)
                           }
                           "14" -> {
                               holder.itemBinding.docTxt.text = "Large box"
                               holder.itemBinding.icon.setBackgroundResource(R.drawable.ic_largebox_low)
                           }*/
                       }
                   }
                }
            }


        }




    }


    class BindingViewHolder(val itemBinding: ItemDocShowBinding) :
        RecyclerView.ViewHolder(itemBinding.root)




}