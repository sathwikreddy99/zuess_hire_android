package fragments

import android.os.Bundle
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zuess.zuess_android.R
import android.text.Editable
import android.util.Log
import android.widget.ImageView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import viewmodels.locationListAdapter
import java.util.*


class location_select_ui : Fragment() {
    var sessionToken: AutocompleteSessionToken? = null
    lateinit var placesClient : PlacesClient
    val adapter = locationListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(),"AIzaSyCbZH87WuUirnlOYX2dMAvjO6WcEDFGvd4")
        }
        placesClient = Places.createClient(this.requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_select_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingDailog = loadingDailog(requireContext())

        //back button
        val backButton = view.findViewById<ImageView>(R.id.locationSelectBackButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        var chooseLocation = view.findViewById<CardView>(R.id.location_select_choose_location)
        val navController = findNavController()
        chooseLocation.setOnClickListener {
            loadingDailog.showDialog()
            navController.navigate(location_select_uiDirections.actionLocationSelectUi2ToLocationMapsUi2())
        }

        var locationListView = view.findViewById<RecyclerView>(R.id.search_location_list)
//        var searchText = view.findViewById<EditText>(R.id.searchUiSearchBar)

        //*********** did not assign recyler view adapter this is yet to be continued****************************

//        searchText.addTextChangedListener(object : TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if(s.toString().length > 3){
//                    getPlacePredictions(s.toString().lowercase(Locale.getDefault()))
//                }
//            }
//
//        })

//        searchText.setOnClickListener {
//            sessionToken = AutocompleteSessionToken.newInstance()
//        }

    }

    //function to get location predictions
    private fun getPlacePredictions(query: String) {

        // FindAutocompletePredictionsRequest.Builder object as well.
        val bias: LocationBias = RectangularBounds.newInstance(
            LatLng(7.798000, 68.14712),  // SW lat, lng
            LatLng(37.090000, 97.34466) // NE lat, lng
        )

        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        val newRequest = FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(sessionToken)
            .setLocationBias(bias)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setQuery(query)
            .setCountries("IN")
            .build()

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            adapter.setPredictions(predictions)
            Log.i("########predictions", "$predictions")
        }.addOnFailureListener { exception: Exception? ->
            if (exception is ApiException) {
                Log.e("", "Place not found: " + exception.status)
            }
        }
    }

}