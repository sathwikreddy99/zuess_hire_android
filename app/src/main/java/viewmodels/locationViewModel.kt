package viewmodels

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class locationViewModel : ViewModel() {
    val location : MutableLiveData<LatLng> by lazy {
        MutableLiveData<LatLng>()
    }
}