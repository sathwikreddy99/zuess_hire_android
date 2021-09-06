package viewmodels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.zuess.zuess_android.R

class locationSelectViewModel : ViewModel() {
    var sessionToken : AutocompleteSessionToken? = null
    fun getSessionToken(){
        sessionToken = AutocompleteSessionToken.newInstance()
    }

}

class locationListAdapter() : RecyclerView.Adapter<locationListAdapter.viewHolder>() {
    private val predictions: MutableList<AutocompletePrediction> = ArrayList()

    class viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var locationName = view.findViewById<TextView>(R.id.location_name)
        var locationAddress = view.findViewById<TextView>(R.id.location_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_location_list_item, parent, false)
        return locationListAdapter.viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return predictions.size
    }

    fun setPredictions(predictions: List<AutocompletePrediction>?){
        this.predictions.clear()
        this.predictions.addAll(predictions!!)
        notifyDataSetChanged()
    }
}