package com.zoom2u_customer.ui.application.chat.message

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.zoom2u_customer.databinding.ItemMessageBinding
import java.util.*

abstract class FirebaseListAdapter<T>(
    private val mRef: Query?,
    private val mModelClass: Class<T>,
    activity: Activity,
    private val mLayout:Int
) :
    BaseAdapter() {
    private val mInflater: LayoutInflater = activity.layoutInflater
    var mModels: MutableList<T> = ArrayList()

    fun cleanup() {
        mRef?.removeEventListener(mListener)
        mModels.clear()
        mKeys.clear()
    }

    override fun getCount(): Int {
        return mModels.size
    }

    override fun getItem(i: Int): T {
        return mModels[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(i: Int, view: View?, parent: ViewGroup): View {

        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        val previousChatTimeStamp:String = if(i==0) {
             ""
         } else
             (mModels[i-1] as Chat).timestamp.toString()
        val model = mModels[i]
        populateView(binding, model, i,previousChatTimeStamp)
        return binding.root
    }


    protected abstract fun populateView(
        binding: ItemMessageBinding,
        model: T,
        i: Int,
        previousChatTimeStamp: String
    )
    fun addItems(dataSnapshot: DataSnapshot, previousChildName: String?) {
        try {
            val model: T? = dataSnapshot.getValue(mModelClass)
            val key: String? = dataSnapshot.key

            if (!mKeys.contains(key.toString())) {
                if (previousChildName == null) {
                    mModels.add(0, model!!)
                    mKeys.add(0, key.toString())
                } else {
                    val previousIndex = mKeys.indexOf(previousChildName)
                    val nextIndex = previousIndex + 1
                    if (nextIndex == mModels.size) {
                        mModels.add(model!!)
                        mKeys.add(key.toString())
                    } else {
                        mModels.add(nextIndex, model!!)
                        mKeys.add(nextIndex, key.toString())
                    }
                }
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkSenderIsAdmin(position: Int): String {
        var senderIsAdminStr = ""
        senderIsAdminStr = (mModels[position] as Chat).sender.toString()
        return senderIsAdminStr
    }

    companion object {
        var mKeys: MutableList<String> = ArrayList()
        lateinit var mListener: ChildEventListener
    }


    init {

        mListener = this.mRef!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val model: T? = dataSnapshot.getValue(this@FirebaseListAdapter.mModelClass)
                    val key: String? = dataSnapshot.key

                    if (!mKeys.contains(key.toString())) {
                        if (previousChildName == null) {
                            mModels.add(0, model!!)
                            mKeys.add(0, key.toString())
                        } else {
                            val previousIndex = mKeys.indexOf(previousChildName)
                            val nextIndex = previousIndex + 1
                            if (nextIndex == mModels.size) {
                                mModels.add(model!!)
                                mKeys.add(key.toString())
                            } else {
                                mModels.add(nextIndex, model!!)
                                mKeys.add(nextIndex, key.toString())
                            }
                        }
                    }
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                // One of the mModels changed. Replace it in our list and name mapping
                val key: String? = dataSnapshot.key
                val newModel: T? = dataSnapshot.getValue(this@FirebaseListAdapter.mModelClass)
                val index = mKeys.indexOf(key.toString())
                if (newModel != null) {
                    mModels[index] = newModel
                }
                notifyDataSetChanged()
            }




            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                try {
//                	String key = dataSnapshot.getKey();
//                	Log.e("", key+"   ============    ");
//					int index = mKeys.indexOf(key);
//					mKeys.remove(index);
//					mModels.remove(index);
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

                // A model changed position in the list. Update our list accordingly
                val key: String? = dataSnapshot.key
                val newModel: T? = dataSnapshot.getValue(this@FirebaseListAdapter.mModelClass)
                val index = mKeys.indexOf(key.toString())
                mModels.removeAt(index)
                mKeys.removeAt(index)
                if (previousChildName == null) {
                    mModels.add(0, newModel!!)
                    mKeys.add(0, key.toString())
                } else {
                    val previousIndex = mKeys.indexOf(previousChildName)
                    val nextIndex = previousIndex + 1
                    if (nextIndex == mModels.size) {
                        mModels.add(newModel!!)
                        mKeys.add(key.toString())
                    } else {
                        mModels.add(nextIndex, newModel!!)
                        mKeys.add(nextIndex, key.toString())
                    }
                }
                notifyDataSetChanged()
            }


            override fun onCancelled(firebaseError: DatabaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur")
            }
        })
    }
}