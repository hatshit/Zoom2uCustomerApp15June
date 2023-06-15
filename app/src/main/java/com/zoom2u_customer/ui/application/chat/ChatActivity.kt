package com.zoom2u_customer.ui.application.chat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityChatBinding
import com.zoom2u_customer.ui.application.chat.message.MessageActivity
import com.zoom2u_customer.utility.AppUtility
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class ChatActivity : AppCompatActivity() {
    lateinit var viewModel: ChatListViewModel
    private var repository: ChatListRepository? = null
    lateinit var binding: ActivityChatBinding
    var customerID = ""

    companion object {
        var adapter: ChatListAdapter? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        setAdapterView(binding)
        viewModel = ViewModelProvider(this)[ChatListViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = ChatListRepository(serviceApi, this)
        viewModel.repository = repository
        if (LoadChatBookingArray.arrayOfChatDelivery.size == 0){
            viewModel.getChatList()
        }else{
            adapter?.updateRecords(LoadChatBookingArray.arrayOfChatDelivery)
        }

        viewModel.getChatListSuccess().observe(this) {
            if (it != null) {
                if (it == "false") {
                    AppUtility.progressBarDissMiss()
                    binding.swipeRefresh.isRefreshing = false
                } else if (it != "") {
                    AppUtility.progressBarDissMiss()
                    binding.swipeRefresh.isRefreshing = false

                    if (LoadChatBookingArray.arrayOfChatDelivery.size > 0) {
                        for (model_deliveriesToChat in LoadChatBookingArray.arrayOfChatDelivery) {
                            FirebaseDatabase.getInstance().reference.child(
                                "customer-courier-booking-chat/" + model_deliveriesToChat.bookingId
                                    .toString() + "_" + model_deliveriesToChat.courierId
                                    .toString() + "/status/courier/unread"
                            )
                                .removeEventListener(model_deliveriesToChat.valueEventListnerOfCourierChat!!)
                            FirebaseDatabase.getInstance().reference.child(
                                "customer-courier-booking-chat/" + model_deliveriesToChat.bookingId
                                    .toString() + "_" + model_deliveriesToChat.courierId
                                    .toString() + "/status/customer/unread"
                            )
                                .removeEventListener(model_deliveriesToChat.valueEventListnerOfCustomerChat!!)
                        }
                        LoadChatBookingArray.arrayOfChatDelivery.clear()
                    }
                    val arrayOfChatDelivery: MutableList<ChatList> = ArrayList()
                    val mainJObjOfChatDelivery: JSONObject = JSONObject(it)
                    if (mainJObjOfChatDelivery.getBoolean("Success")) {
                        val jsonArrayOfChatDelivery =
                            JSONArray(mainJObjOfChatDelivery.getString("data"))
                        if (jsonArrayOfChatDelivery.length() > 0) {
                            customerID =
                                jsonArrayOfChatDelivery.getJSONObject(0).getString("CustomerId")
                            setCourierToOfflineFromChat()
                            for (i in 0 until jsonArrayOfChatDelivery.length()) {
                                val chatList = ChatList(this, jsonArrayOfChatDelivery.getJSONObject(i))
                                arrayOfChatDelivery.add(chatList)
                                LoadChatBookingArray.arrayOfChatDelivery.add(chatList)
                            }
                        }
                    }

                    if (arrayOfChatDelivery.size != 0) {
                        adapter?.updateRecords(arrayOfChatDelivery.reversed().toMutableList())
                    }
                }
            }
        }

        binding.zoom2uHeader.backBtn.setOnClickListener {
            finish()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getChatList()
        }
    }

    /* ***************  Set courier status to offline for chat *********** */
    private fun setCourierToOfflineFromChat() {
        if (customerID != "" && MessageActivity.mFirebaseRef != null)
            MessageActivity.mFirebaseRef?.child(
                "/customers/$customerID/status/online"
            )?.onDisconnect()?.setValue(0)
    }

    fun setAdapterView(binding: ActivityChatBinding) {
        val layoutManager = GridLayoutManager(this, 1)
        binding.chatView.layoutManager = layoutManager
        adapter = ChatListAdapter(this, onItemClick = ::onItemClick)
        binding.chatView.adapter = adapter
    }

    private fun onItemClick(chatList: ChatList) {
        val intent = Intent(this, MessageActivity::class.java)
        intent.putExtra("chat", chatList)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        LoadChatBookingArray.setCourierToOnlineForChat();
        if (adapter != null)
            adapter?.notifyDataSetChanged()
    }
}


