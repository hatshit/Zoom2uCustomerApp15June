package com.zoom2u_customer.ui.application.chat.bid_chat.active_bid_chat

import android.content.Context
import android.content.Intent
import android.database.DataSetObserver
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.zoom2u_customer.R
import com.zoom2u_customer.databinding.ActivityBidMessageBinding
import com.zoom2u_customer.services.ServiceNotifyCourierAboutChat
import com.zoom2u_customer.ui.application.chat.bid_chat.BidChat
import com.zoom2u_customer.ui.application.chat.bid_chat.BidFirebaseListAdapter
import com.zoom2u_customer.utility.AppUtility
import java.text.SimpleDateFormat
import java.util.*

class ActiveBidMessageActivity : AppCompatActivity() {
    lateinit var binding: ActivityBidMessageBinding
    private var chatList: ActiveBidChatList? = null
    var chatItemCountForNotificationSound = 0
    companion object {
        var mFirebaseRef: DatabaseReference? = null
        var messageListAdapter: ActiveBidMessageListAdapter? = null
    }


    var mConnectedListener: ValueEventListener? = null
    var unreadMessageCountForCourier: Long = 0
    var chatItemCount = 1
    var isCourierOnline: Long = 0
    var isTyping = false
    val TYPING_TIMEOUT = 2000 // 5 seconds timeout
    var isChatBookingScreenOpne = false
    val timeoutHandler = Handler()
    var pathFirebase: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bid_message)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        // AppUtility.hideKeyBoardClickOutside(binding.messageListView, this)
        mFirebaseRef = FirebaseDatabase.getInstance().reference
        if(intent.hasExtra("BidChat")){
            chatList = intent.getParcelableExtra("BidChat")
            setDataView(chatList)
        }
        isChatBookingScreenOpne = true
        inChatView()


        if (!TextUtils.isEmpty(chatList?.courierImage))
            Picasso.get().load(chatList?.courierImage)
                .placeholder(R.drawable.profile) // optional
                .into(binding.dp)




        binding.callBtn.setOnClickListener {
            if (chatList?.courierMobile != null && !chatList?.courierMobile
                    .equals("")
            ) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + chatList?.courierMobile)
                startActivity(intent)
            } else
                Toast.makeText(this, "Sorry! Contact number is not available", Toast.LENGTH_SHORT).show()
        }

    }

    private fun inChatView() {

        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.sendMsgImg.setOnClickListener {
            sendMessage()
        }


        val typingTimeout = Runnable {
            isTyping = false
            mFirebaseRef?.child(
                "$pathFirebase/status/customer/typing"
            )?.setValue(0)
        }

        binding.msgText.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_NULL && keyEvent.action == KeyEvent.ACTION_DOWN) {
                sendMessage()
            }
            true
        }

/*make courier unread count 0*/
        FirebaseDatabase.getInstance().reference.child(
            ("quote-request-comments/" + chatList?.bookingId
                .toString() + "_" +
                    chatList?.courierId+ "/status/courier/unread")
        ).setValue(0)





        binding.msgText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                timeoutHandler.removeCallbacks(typingTimeout)
                if (binding.msgText.text.toString().trim { it <= ' ' }.isNotEmpty()) {
                    timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT.toLong())
                    if (!isTyping) {
                        isTyping = true
                        mFirebaseRef?.child(
                            "$pathFirebase/status/customer/typing"
                        )?.setValue(1)
                    }
                } else {
                    isTyping = false
                    mFirebaseRef?.child(
                        ("$pathFirebase/status/customer/typing")
                    )?.setValue(0)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        binding.messageListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (firstVisibleItem == 0) {
                    // check if we reached the top or bottom of the list
                    val offset: Int = if (binding.messageListView.getChildAt(0) == null)
                        0
                    else binding.messageListView.getChildAt(0).top
                    if (offset == 0) {
                        if (BidFirebaseListAdapter.mKeys.size > 14 * chatItemCount) {
                            val childUrlStr: String =
                                "$pathFirebase/message"
                            val queryRef = mFirebaseRef?.child(childUrlStr)?.orderByKey()?.endAt(
                                BidFirebaseListAdapter.mKeys[0]
                            )?.limitToLast(15)
                            queryRef?.addChildEventListener(object : ChildEventListener {
                                override fun onChildRemoved(arg0: DataSnapshot) {}
                                override fun onChildMoved(arg0: DataSnapshot, arg1: String?) {}
                                override fun onChildChanged(
                                    arg0: DataSnapshot,
                                    arg1: String?
                                ) {
                                }

                                override fun onChildAdded(arg0: DataSnapshot, arg1: String?) {
                                    messageListAdapter?.addItems(arg0, arg1)
                                }

                                override fun onCancelled(arg0: DatabaseError) {}
                            })
                            binding.messageListView.clearFocus()
                            binding.messageListView.setSelection(15)
                            chatItemCount++
                        }
                    }
                }
            }
        })

    }

    private fun setDataView(chatList: ActiveBidChatList?) {
        binding.route.text = chatList?.pickupSuburb + " → " + chatList?.dropSuburb
        binding.name.text = chatList?.courier
        pathFirebase = "quote-request-comments/" + chatList?.bookingId.toString() + "_" +
                chatList?.courierId.toString()
    }

    override fun onStart() {
        super.onStart()

        binding.messageListView.cacheColorHint = Color.TRANSPARENT
        binding.messageListView.isScrollingCacheEnabled = false
        binding.messageListView.isAnimationCacheEnabled = false

        messageListAdapter = ActiveBidMessageListAdapter(
            mFirebaseRef?.child(
                "$pathFirebase/message"
            )!!.limitToLast(15),
            this@ActiveBidMessageActivity,
            "customer",
            R.layout.item_message,
            chatList
        )
        binding.messageListView.adapter = messageListAdapter

        messageListAdapter?.registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                super.onChanged()
                binding.messageListView.setSelection((messageListAdapter?.count)?.minus(1)!!.toInt())
            }
        })


        mConnectedListener = mFirebaseRef?.root?.child(".info/connected")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    /*  Toast.makeText(
                          this@MessageActivity,
                          "You are connected to chat",
                          Toast.LENGTH_LONG
                      ).show();*/
                }

                override fun onCancelled(firebaseError: DatabaseError) {
                    /*Toast.makeText(
                        this@MessageActivity,
                        "You are disconnected from chat",
                        Toast.LENGTH_LONG
                    ).show()*/
                }
            })


        mFirebaseRef?.child(
            "$pathFirebase/status/customer/unread"
        )?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                try {
                    if (arg0.value != null)
                        unreadMessageCountForCourier = arg0.value as Long
                } catch (e: Exception) {
                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        })


        mFirebaseRef?.child(
            "/couriers/" + chatList?.courierId + "/status/online"
        )?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                try {
                    if (arg0.value != null) {
                        isCourierOnline = arg0.value as Long
                        if (isCourierOnline == 1L) {
                            binding.status.text = "Online"
                            binding.status.visibility = View.VISIBLE
                        } else {
                            binding.status.visibility = View.GONE
                        }
                        chatItemCountForNotificationSound = messageListAdapter?.count!! + 1
                    }
                } catch (e: Exception) {
                    //	e.printStackTrace();
                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        })


        mFirebaseRef?.child(
            "$pathFirebase/status/courier/typing"
        )?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                try {
                    if (arg0.value != null) {
                        val isTyping = arg0.value as Long
                        when {
                            isTyping == 1L -> {
                                binding.status.text = "Typing..."
                                binding.status.visibility = View.VISIBLE
                            }
                            isCourierOnline == 1L -> {
                                binding.status.text = "Online"
                                binding.status.visibility = View.VISIBLE
                            }
                            else -> binding.status.visibility = View.GONE
                        }
                        chatItemCountForNotificationSound = messageListAdapter?.count!! + 1
                    }
                } catch (e: Exception) {
                    //	e.printStackTrace();
                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        })


        mFirebaseRef?.child(
            "$pathFirebase/message"
        )?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(arg0: DataSnapshot) {
                try {
                    if (isChatBookingScreenOpne)
                        binding.messageListView.setSelection(
                            messageListAdapter?.count!!
                        )
                } catch (e: Exception) {
                    Log.d("firebaseError","${e.message}")

                }
            }

            override fun onCancelled(arg0: DatabaseError) {}
        })


    }

    override fun onResume() {
        super.onResume()
        chatItemCount = 1
        ActiveBidLoadChatBookingArray.setCourierToOnlineForChat()
    }

    private fun sendMessage() {
        val inputMsgTxt: String = binding.msgText.text.toString()
        if (inputMsgTxt != "") {
            // unreadMessageCountForCourier++
            val currentDate: String = sendTimeToServer()
            val chat = BidChat(inputMsgTxt, "customer", 0, currentDate,"")
            mFirebaseRef?.child("$pathFirebase/message")
                ?.push()?.setValue(chat)?.addOnSuccessListener {
                    Log.d("firebaseError","Messaged inserted: ${pathFirebase.toString()}")
                }?.addOnFailureListener {
                    Log.d("firebaseError","${it.message}")
                }
            mFirebaseRef?.child(("$pathFirebase/status/customer/unread"))
                ?.setValue(unreadMessageCountForCourier)




            FirebaseDatabase.getInstance().reference.child(
                ("quote-request-comments/" + chatList?.bookingId
                    .toString() + "_" +
                        chatList?.courierId+ "/status/customer/unread")
            ).get().addOnSuccessListener {
                val unread=it.value as Long+1

                FirebaseDatabase.getInstance().reference.child(
                    ("quote-request-comments/" + chatList?.bookingId
                        .toString() + "_" +
                            chatList?.courierId+ "/status/customer/unread")
                ).setValue(unread)
            }





            if (isCourierOnline == 0L) {
                val notifyCustomerChat = Intent(this@ActiveBidMessageActivity, ServiceNotifyCourierAboutChat::class.java)
                notifyCustomerChat.putExtra("CourierId", chatList?.courierId)
                notifyCustomerChat.putExtra("Message", inputMsgTxt)
                startService(notifyCustomerChat)

            }
            binding.msgText.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    private fun sendTimeToServer(): String {
        var dateStr = ""
        try {
            val converter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            converter.timeZone = TimeZone.getTimeZone("UTC")
            val convertedDate = Date()
            try {
                dateStr = converter.format(convertedDate).toString()
                return dateStr
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dateStr
    }

    override fun onStop() {
        super.onStop()
        mFirebaseRef?.root?.child(".info/connected")?.removeEventListener(mConnectedListener!!)
        messageListAdapter?.cleanup()
    }


}
