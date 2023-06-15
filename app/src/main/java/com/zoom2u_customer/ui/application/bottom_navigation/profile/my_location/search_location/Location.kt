package com.zoom2u_customer.ui.application.bottom_navigation.profile.my_location.search_location

import androidx.appcompat.app.AppCompatActivity

class Location : AppCompatActivity() {
  /*  lateinit var binding: ActivityLocationBinding
    var sessionToken: AutocompleteSessionToken? = null

    private var placesClient: PlacesClient? = null

    var placeModel: MutableList<Place_Model> = ArrayList()
    var adapter: PlaceAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location)




        if (!Places.isInitialized()) {
            val apiKey = this.getString(R.string.Acgoogle_api_key)
            Places.initialize(this.applicationContext, apiKey)
        }
        sessionToken = AutocompleteSessionToken.newInstance()
        placesClient = Places.createClient(this)


        val arrayPfAddress_N_Fa = ArrayList<Place_Model>()

        val layoutManager = GridLayoutManager(this, 1)
        binding.placeRecycler.layoutManager = layoutManager

        adapter =
            PlaceAdapter(this, arrayPfAddress_N_Fa, object : PlaceAdapter.OnItemClickListener {
                override fun onItemClick(item: Place_Model) {

                }
            })
        binding.placeRecycler.adapter = adapter


        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.count() > 0) {
                    binding.placeRecycler.visibility = View.VISIBLE
                    placeModel.clear()
                    placeAPICallForAddress(binding.searchText.text.toString())
                } else
                    binding.searchText.visibility = View.GONE
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

                stringify(it, false)

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

    fun stringify(response: FindAutocompletePredictionsResponse, raw: Boolean) {
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
*/
}