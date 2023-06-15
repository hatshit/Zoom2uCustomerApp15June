package com.zoom2u_customer.ui.application.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatListViewModel : ViewModel(){

    var success: MutableLiveData<String> = MutableLiveData("")
    var repository: ChatListRepository? = null

    fun getChatList() = repository?.getChatList(onSuccess = ::onSuccess)

    fun onSuccess(ChatList:String){
        success.value=ChatList
    }


    fun getChatListSuccess(): MutableLiveData<String> {
        return success
    }
}