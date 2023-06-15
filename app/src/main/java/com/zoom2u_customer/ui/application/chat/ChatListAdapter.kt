package com.zoom2u_customer.ui.application.chat

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatListAdapter(val context: Context, private val onItemClick: (ChatList) -> Unit) :
    RecyclerView.Adapter<ChatListAdapter.BindingViewHolder>() {

    val dataList: MutableList<ChatList> = ArrayList()
    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateRecords(dataList1: MutableList<ChatList>) {
        dataList.clear()
        this.dataList.addAll(dataList1)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemChatBinding =
            ItemChatBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        if (position == itemCount-1)
            holder.itemBinding.blankView.visibility = View.VISIBLE
        else
            holder.itemBinding.blankView.visibility = View.GONE


        val chat: ChatList = dataList[position]
        holder.itemBinding.chat = chat

        holder.itemBinding.route.text = chat.pickupSuburb + " → " + chat.dropSuburb
        if (chat.dropDateTime != null && chat.dropDateTime != ""
            && chat.dropDateTime != "null"
        ) {
            holder.itemBinding.dueTime.text = "Due by " + getTimeToLocalTimeStamp(chat.dropDateTime.toString())
            holder.itemBinding.dueDate.text = getDateToLocalTimeStamp(chat.dropDateTime.toString())
        }

        if (!TextUtils.isEmpty(chat.courierImage))
            Picasso.get().load(chat.courierImage)
                .placeholder(R.drawable.profile) // optional
                .into(holder.itemBinding.dp)

        if (chat.unreadMsgCountOfCourier > 0) {
            holder.itemBinding.badgeCount.visibility = View.VISIBLE
            holder.itemBinding.badgeCount.text=chat.unreadMsgCountOfCourier.toString()
        }else
            holder.itemBinding.badgeCount.visibility = View.GONE

        holder.itemBinding.root.setOnClickListener {
            onItemClick(chat)
        }

        if(chat.isCourierOnline==1L){
            holder.itemBinding.isOnline.visibility = View.VISIBLE
        }
        else
            holder.itemBinding.isOnline.visibility = View.GONE

    }


    class BindingViewHolder(val itemBinding: ItemChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    @SuppressLint("SimpleDateFormat")
    fun getTimeToLocalTimeStamp(timeStamp: String): String {
        var dateStr = ""
        if (timeStamp != "") {
            val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            converter.timeZone = TimeZone.getTimeZone("IST")
            val convertedDate: Date? = converter.parse(timeStamp)
            val dateFormatter = SimpleDateFormat("hh:mm a")
            dateStr = dateFormatter.format(convertedDate).toString()

        }
        return dateStr
    }


    @SuppressLint("SimpleDateFormat")
    fun getDateToLocalTimeStamp(timeStamp: String): String {
        var dateStr = ""
        if (timeStamp != "") {
            val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            converter.timeZone = TimeZone.getTimeZone("IST")
            val convertedDate: Date? = converter.parse(timeStamp)
            val dateFormatter = SimpleDateFormat("MMM d, yyyy")
            dateStr = dateFormatter.format(convertedDate).toString()

        }
        return dateStr
    }

    override fun getItemViewType(position: Int): Int = position
}