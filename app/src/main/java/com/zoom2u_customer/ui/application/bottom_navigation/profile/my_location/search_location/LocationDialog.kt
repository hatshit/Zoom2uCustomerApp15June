package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.search_location

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.zoom2u_customer.R

class LocationDialog(private val context: Context){

    private var sessionToken: AutocompleteSessionToken? = null
    private var placesClient: PlacesClient? = null
    private var placeModel: MutableList<Place_Model> = ArrayList()
    private var adapter: PlaceAdapter? = null

   init {
       if (!Places.isInitialized()) {
           val apiKey = context.getString(R.string.google_api_ke)
           Places.initialize(context.applicationContext, apiKey)
       }
       sessionToken = AutocompleteSessionToken.newInstance()
       placesClient = Places.createClient(context)
   }




    fun showLocationDialog(context: Context?) {

        val viewGroup = (context as Activity).findViewById<ViewGroup>(R.id.content)

        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.activity_location, viewGroup, false)

        val builder = AlertDialog.Builder(context)

        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        val searchText :EditText = dialogView.findViewById(R.id.search_text)
        val placeRecycler : RecyclerView = dialogView.findViewById(R.id.place_recycler)


        val place = ArrayList<Place_Model>()

        val layoutManager = GridLayoutManager(context, 1)
        placeRecycler.layoutManager = layoutManager

        adapter =
            PlaceAdapter(context, place, object : PlaceAdapter.OnItemClickListener {
                override fun onItemClick(item: Place_Model) {

                }
            })
        placeRecycler.adapter = adapter


        searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.count() > 0) {
                    placeRecycler.visibility = View.VISIBLE
                    placeModel.clear()
                    placeAPICallForAddress(searchText.text.toString())
                } else
                   placeRecycler.visibility = View.GONE
            }
        })


    }

    private fun placeAPICallForAddress(searchString: String) {
        try {
            var requestBuilder =
                FindAutocompletePredictionsRequest.builder()
                    .setQuery(searchString)
                    .setCountry("AU")
                    .setTypeFilter(TypeFilter.ADDRESS)

            requestBuilder.sessionToken = sessionToken

            var task = placesClient?.findAutocompletePredictions(requestBuilder.build())
            task?.addOnSuccessListener(OnSuccessListener {

                updateData(it, false)

            })

            task?.addOnFailureListener(OnFailureListener {
                it.printStackTrace()
            })

            task?.addOnCompleteListener(OnCompleteListener {

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateData(response: FindAutocompletePredictionsResponse, raw: Boolean) {
        for (autocompletePrediction: AutocompletePrediction in response.autocompletePredictions) {
            loadData(
                Place_Model(
                    0,
                    autocompletePrediction.getPrimaryText(null).toString(),
                    autocompletePrediction.getFullText(null).toString(),
                    autocompletePrediction.getSecondaryText(null).toString(),
                    autocompletePrediction.distanceMeters
                )
            )
        }
        adapter?.updateRecords(placeModel)
    }

    private fun loadData(modal_StopShareData: Place_Model) {
        placeModel.add(modal_StopShareData)

    }

}