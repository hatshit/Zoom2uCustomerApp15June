package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.model.MyLocationResAndEditLocationReq

class ContactNameAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val contactList: List<MyLocationResAndEditLocationReq>):
    ArrayAdapter<MyLocationResAndEditLocationReq>(context, layoutResource, contactList),
    Filterable {
    private var contact: List<MyLocationResAndEditLocationReq> = contactList

    override fun getCount(): Int {
        return contact.size
    }

    override fun getItem(p0: Int): MyLocationResAndEditLocationReq {
        return contact[p0]

    }
    override fun getItemId(p0: Int): Long {
        // Or just return p0
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = contact[position].Location?.ContactName.toString()
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                contact = filterResults.values as List<MyLocationResAndEditLocationReq>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                   contactList
                else
                    contactList.filter {
                        it.Location?.ContactName?.toLowerCase()?.contains(queryString) == true
                    }

                return filterResults
            }

        }
    }
}