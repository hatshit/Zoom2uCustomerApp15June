package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.search_location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.zoom2u_customer.R

class MyPlaceAdapter(var context: Context?, var counting: MutableList<Place_Model>) : BaseAdapter() {

    private var inflator: LayoutInflater? = null
    private var mCounting: MutableList<Place_Model>

    init {
        inflator = LayoutInflater.from(context)
        mCounting = counting
    }

    override fun getCount(): Int {
        return mCounting.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view = inflator?.inflate(R.layout.dropdown_item,null)
        val tv: TextView?= view?.findViewById(R.id.textView)
        tv?.text = mCounting[position].getPlaceName().toString()

        return view
    }
/*
    override fun getDropDownView(position: Int, convert: View, parent: ViewGroup?): View? {
    val convertView :View? = super.getDropDownView(position, convert, parent)
        convertView?.visibility = View.VISIBLE
        var p = convertView?.layoutParams
        if (p != null) {
            p.width = ViewGroup.LayoutParams.MATCH_PARENT
            p.height = ViewGroup.LayoutParams.WRAP_CONTENT
        } else p = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        convertView?.layoutParams = p
        return convertView
    }*/
}