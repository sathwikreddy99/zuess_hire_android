package viewmodels

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

 var latLng : LatLng = LatLng(0.0,0.0)

class locationViewModel : ViewModel() {
    val location : MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }
    fun setLatLng(){

    }

    fun returnLatLng(): LatLng {
        return latLng
    }

}