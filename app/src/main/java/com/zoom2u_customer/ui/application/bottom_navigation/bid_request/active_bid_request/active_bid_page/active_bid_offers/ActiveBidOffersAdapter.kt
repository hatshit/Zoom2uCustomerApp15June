package com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.active_bid_offers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.zoom2u_customer.R


import com.zoom2u_customer.databinding.ItemActiveBidOffersBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.active_bid_page.Offer
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidChatList
import com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat.ActiveBidMessageActivity
import com.zoom2u_customer.utility.AppUtility
import org.json.JSONException


class ActiveBidOffersAdapter(
    val context: Context, private val dataList: MutableList<Offer>,
    private val onItemClick: (Offer) -> Unit,
    private val onChatClick: (ActiveBidChatList) -> Unit,
    private val onRejectBidClick: (Offer) -> Unit,
    val isHeavyFreight:Boolean
) :
    RecyclerView.Adapter<ActiveBidOffersAdapter.BindingViewHolder>() {
    private var isBidAvailable: Boolean = true
    private val chatDataList: MutableList<ActiveBidChatList> = ArrayList()
    override fun getItemCount(): Int {
        return dataList.size
    }

    fun updateRecords(dataList1: MutableList<ActiveBidChatList>) {
        this.chatDataList.addAll(dataList1)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val rootView: ItemActiveBidOffersBinding =
            ItemActiveBidOffersBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewHolder(rootView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {

        val activeBidOffers: Offer = dataList[position]
        holder.itemBinding.activebidoffers = activeBidOffers

        if(isHeavyFreight){
            holder.itemBinding.lastCompleted.visibility=View.GONE
            holder.itemBinding.lastCompletedTime.visibility=View.GONE
            holder.itemBinding.register.visibility=View.GONE
            holder.itemBinding.registerTime.visibility=View.GONE
            holder.itemBinding.expireTime.visibility=View.GONE
        }


        holder.itemBinding.name.text = activeBidOffers.Courier.toString()
        holder.itemBinding.price.text = "$" + activeBidOffers.Price.toString()

        if (!TextUtils.isEmpty(activeBidOffers.CourierImage)) {
            holder.itemBinding.dp.setImageBitmap(AppUtility.getBitmapFromURL(activeBidOffers.CourierImage))
        }


        if (!TextUtils.isEmpty(activeBidOffers.Notes)) {
            holder.itemBinding.notes.text = activeBidOffers.Notes
        }else
            holder.itemBinding.notes.text = "-"

        holder.itemBinding.acceptBid.setOnClickListener {
            if (isBidAvailable)
                onItemClick(dataList[position])
            else
                Toast.makeText(context, "This bid offer is expired.", Toast.LENGTH_SHORT).show()
        }

        holder.itemBinding.rejectBid.setOnClickListener {
            onRejectBidClick(dataList[position])
        }


        /**extra details data*/
        holder.itemBinding.pickupTime.text =
            AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].PickupETA)
        holder.itemBinding.lastCompletedTime.text =
            AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].LastCompletedDeliveryDateTime)
        holder.itemBinding.dropTime.text =
            AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].DropETA)
        holder.itemBinding.registerTime.text =
            AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].RegisteredWithZoom2uOnDateTime)
        holder.itemBinding.bidHasTime.text =
            AppUtility.getDateTimeFromDeviceToServerForDate(dataList[position].BidActivePeriod)


        /**if BidActivePeriod complete 30min*/
        if (System.currentTimeMillis() > AppUtility.getDateTime(dataList[position].BidActivePeriod).time) {
            isBidAvailable = false
            holder.itemBinding.pickupTime.setTextColor(Color.RED)
            holder.itemBinding.lastCompletedTime.setTextColor(Color.RED)
            holder.itemBinding.dropTime.setTextColor(Color.RED)
            holder.itemBinding.registerTime.setTextColor(Color.RED)
            holder.itemBinding.bidHas.text = "Bid has :"
            holder.itemBinding.bidHasTime.text = "Expired"
            holder.itemBinding.bidHasTime.setTextColor(Color.RED)
            holder.itemBinding.bidExpireNote.visibility = View.VISIBLE
            holder.itemBinding.acceptBid.setBackgroundResource(R.drawable.expire_bid)
            holder.itemBinding.acceptBid.text = "Bid Expired"
            holder.itemBinding.rejectBid.visibility=View.GONE

        }else holder.itemBinding.rejectBid.visibility=View.VISIBLE

        if (isHeavyFreight)
        holder.itemBinding.rejectBid.visibility=View.GONE


        val chat: ActiveBidChatList = chatDataList[position]

        if (chat.unreadMsgCountOfCourier > 0) {
            holder.itemBinding.badgeCount.visibility = View.VISIBLE
            holder.itemBinding.badgeCount.text=chat.unreadMsgCountOfCourier.toString()
        }else
            holder.itemBinding.badgeCount.visibility = View.GONE

        if( isHeavyFreight)
        {
            holder.itemBinding.chat.visibility=View.GONE
            holder.itemBinding.chatBox.visibility=View.GONE
        }
        else
        {
            holder.itemBinding.chatBox.visibility = View.VISIBLE
            holder.itemBinding.chat.visibility = View.VISIBLE

            holder.itemBinding.chat.setOnClickListener {
                try {
                    val context1 = holder.itemBinding.root.context
                    val intent = Intent(context1, ActiveBidMessageActivity::class.java)
                    intent.putExtra("BidChat", chatDataList[position])
                    context1.startActivity(intent)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            if (activeBidOffers.IsCancel==true){

                holder.itemBinding.chatBox.alpha = 0.7f

                holder.itemBinding.chat.apply {
                    isClickable=false
                    alpha=0.7f
                }

                //status button
                holder.itemBinding.acceptBid.apply {
                    isClickable=false
                    alpha=0.7f
                    text="Cancelled"
                }

                //reject button
                holder.itemBinding.rejectBid.apply {
                    isClickable=false
                    alpha=0.7f
                }


            }else {
                holder.itemBinding.chatBox.alpha = 1f
                holder.itemBinding.chat.apply {
                    isClickable=true
                    alpha=1f
                }

                //accept button
                holder.itemBinding.acceptBid.apply {
                    isClickable=true
                    alpha=1f
                }

                //reject button
                holder.itemBinding.rejectBid.apply {
                    isClickable=true
                    alpha=1f
                }
            }
        }




        if(chat.isCourierOnline==1L){
            holder.itemBinding.isOnline.visibility = View.VISIBLE
        }
        else
            holder.itemBinding.isOnline.visibility = View.GONE


        holder.itemBinding.loadMore.setOnClickListener {
            holder.itemBinding.loadMore.visibility = View.GONE
            holder.itemBinding.loadData.visibility = View.VISIBLE
        }
        holder.itemBinding.loadLess.setOnClickListener {
            holder.itemBinding.loadMore.visibility = View.VISIBLE
            holder.itemBinding.loadData.visibility = View.GONE
        }

    }

    @SuppressLint("SuspiciousIndentation")
    fun removeOffer(offer: Offer) {
    if (dataList.isNullOrEmpty())
        return

        dataList.remove(offer)
        notifyDataSetChanged()
    }


    class BindingViewHolder(val itemBinding: ItemActiveBidOffersBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


}