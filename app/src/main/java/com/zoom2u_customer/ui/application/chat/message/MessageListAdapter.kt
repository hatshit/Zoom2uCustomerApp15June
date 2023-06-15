package com.zoom2u_customer.ui.application.chat.message


import android.annotation.SuppressLint
import android.app.Activity
import android.text.TextUtils
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ItemMessageBinding
import com.zoom2u_customer.ui.application.chat.ChatList
import java.text.SimpleDateFormat
import java.util.*

class MessageListAdapter(
    ref: Query?,
    var activity: Activity,
    private val mUsername: String,
    mLayout: Int,
    chatList: ChatList?
) : FirebaseListAdapter<Chat>(ref, Chat::class.java, activity, mLayout) {

    var chatList:ChatList?=chatList




    override fun populateView(
        binding: ItemMessageBinding,
        chat: Chat,
        i: Int,
        previousChatTimeStamp: String
    ) {

        val author: String? = chat.sender

        val chatDate:String = getDateToLocalTimeStamp(chat.timestamp.toString())
        binding.chatDate.text = getDateToLocalTimeStamp(chat.timestamp.toString())


        if (chat.receipt == 1)
            binding.readCheck.setImageResource(R.drawable.receipt_read)
        else
            binding.readCheck.setImageResource(R.drawable.receipt_unread)


        if (!TextUtils.isEmpty(chatList?.courierImage))
            Picasso.get().load(chatList?.courierImage)
                .placeholder(R.drawable.profile) // optional
                .into(binding.rcvDp)
        if (!TextUtils.isEmpty(chatList?.customerImage))
            Picasso.get().load(chatList?.customerImage)
            .placeholder(R.drawable.profile) // optional
            .into(binding.senderDp)

        if(i==0) {
            binding.chatDate.visibility = View.VISIBLE
            binding.chatDate.visibility = View.VISIBLE
            val sdf = SimpleDateFormat("MMM d, yyyy")
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, -1)
            val yesterdayDate = sdf.format(cal.time)
            val currentDate = sdf.format(Date())
            when (chatDate) {
                currentDate -> binding.chatDate.text = "Today"
                yesterdayDate -> binding.chatDate.text = "Yesterday"
                else -> binding.chatDate.text = getDateToLocalTimeStamp(chat.timestamp.toString())
            }
        }else{
            val previousChatDate=getDateToLocalTimeStamp(previousChatTimeStamp)
            if(previousChatDate!=chatDate){
                binding.chatDate.visibility = View.VISIBLE
                val sdf = SimpleDateFormat("MMM d, yyyy")
                val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, -1)
                val yesterdayDate = sdf.format(cal.time)
                val currentDate = sdf.format(Date())
                when (chatDate) {
                    currentDate -> binding.chatDate.text = "Today"
                    yesterdayDate -> binding.chatDate.text = "Yesterday"
                    else -> binding.chatDate.text = getDateToLocalTimeStamp(chat.timestamp.toString())
                }
            }else
                binding.chatDate.visibility = View.GONE
        }

        if (author != null && author == mUsername) {
            binding.rcvLayout.visibility = View.GONE
            binding.senderLayout.visibility = View.VISIBLE
            binding.senderMsg.text = chat.message
            binding.sendTime.text = getTimeToLocalTimeStamp(chat.timestamp.toString())
        } else {
            binding.senderLayout.visibility = View.GONE
            binding.rcvLayout.visibility = View.VISIBLE
            binding.rcvMsg.text = chat.message
            binding.reciveTime.text = getTimeToLocalTimeStamp(chat.timestamp.toString())
        }
        setUnreadUrlToSetUnreadCount(chat,i)
    }


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


    /** Set unread message count to server when readed **/
    private fun setUnreadUrlToSetUnreadCount(chat: Chat, position: Int) {
        if (chat.receipt === 0) {
            val hMap = HashMap<String, Any>()
            hMap["message"] = chat.message.toString()
            hMap["receipt"] = 0
            hMap["sender"] = chat.sender.toString()
            hMap["timestamp"] = chat.timestamp.toString()
            FirebaseDatabase.getInstance().reference.child(
                "customer-courier-booking-chat/" + chatList?.bookingId
                    .toString() + "_" + chatList?.courierId
                    .toString() + "/message/" + mKeys[position]
            ).updateChildren(hMap)
            FirebaseDatabase.getInstance().reference.child(
                ("customer-courier-booking-chat/" + chatList?.bookingId
                    .toString() + "_" +
                       chatList?.courierId+ "/status/courier/unread")
            ).setValue(0)
        }
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
}