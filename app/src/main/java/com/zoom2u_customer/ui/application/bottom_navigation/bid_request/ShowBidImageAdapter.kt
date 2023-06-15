package com.zoom2u_customer.ui.application.bottom_navigation.bid_request


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.zoom2u_customer.databinding.BidImagesShowBinding



class ShowBidImageAdapter(val context : Context?, private val dataList: List<String>,
                          private val onItemClick:(String) -> Unit) : RecyclerView.Adapter<ShowBidImageAdapter.BindingViewHolder>() {


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: BidImagesShowBinding =
            BidImagesShowBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val imagePath: String = dataList[position]
        Picasso.get().load(imagePath).into(holder.itemBinding.imagePath)



        holder.itemBinding.root.setOnClickListener {
            onItemClick(imagePath)
        }
    }


    class BindingViewHolder(val itemBinding: BidImagesShowBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}