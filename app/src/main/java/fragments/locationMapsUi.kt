package fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.libraries.places.api.Places
import com.google.android.material.snackbar.Snackbar
import com.zuess.zuess_android.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import android.animation.ValueAnimator

import android.view.animation.AccelerateDecelerateInterpolator

import android.animation.IntEvaluator
import android.animation.ValueAnimator.AnimatorUpdateListener

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.graphics.drawable.GradientDrawable
import com.google.android.gms.maps.model.*


class locationMapsUi : Fragment(), OnMapReadyCallback{
    //variable
    var map : GoogleMap? = null
    var marker : Marker? = null
    val location : MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }
    val mapCentre : MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }
    var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_maps, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing
        this.context?.let { Places.initialize(it,getString(R.string.maps_api_key)) }
        var placesClient = this.context?.let { Places.createClient(it) }
        //initialising map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.location_map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        //checking if location permissions are granted or not
        if (
            this.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            != PackageManager.PERMISSION_GRANTED && this.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            //requesting permission when the user denied it at first
            if(this.activity?.let { ActivityCompat.shouldShowRequestPermissionRationale(it,Manifest.permission.ACCESS_FINE_LOCATION) } == true){
                Snackbar.make(view,"enable location services",Snackbar.LENGTH_LONG)
                    .setAction("ok",
                         View.OnClickListener{
                            requestPermission()
                        }
                    )
            }else{
                //request permission if user did not deny at first
                Log.i("","requesting permission")
                requestPermission()

            }
            Log.i("","Permission not granted")
        }else {
            //to get location when permission is granted
            getLocation()

        }


    }


    //getting the last known location
    @SuppressLint("MissingPermission")
    fun getLocation(){
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { result ->
                if(result != null) {
                    Log.i("location:", "${result.latitude},${result.longitude}")
                    location.value = result
                    Log.i("","got location $location")
                }else{
                    Log.i("","The result is null $fusedLocationClient")
                }
            }
    }


    //requesting for permission when not denied
    fun requestPermission(){
        ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            34)
        Log.i("","in request permissions")
    }


    //callback when user chooses the permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 34){
            if(grantResults.isEmpty()){
                Log.i("", "User interaction was cancelled.")

            }
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                getLocation()
            }else{
                view?.let { Snackbar.make(it,"enable location services",Snackbar.LENGTH_LONG)
                    .setAction("settings",View.OnClickListener {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    })
                }

            }
        }
    }

    //setting up my location when map i ready
    @SuppressLint("MissingPermission")
    fun setLocationOnMap(){
        Log.i("","map variable $map, $location")
        if(location != null){
            var marker1 : LatLng? = location.value?.let { LatLng(it.latitude,it.longitude) }
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                marker1, 15F
            )
            )
            marker = map?.addMarker(
                MarkerOptions()
                    .position(marker1)
                    .draggable(true)
            )
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
        }
    }

    //on map ready function
    override fun onMapReady(map : GoogleMap) {
        this.map = map
        var latLng : LatLng
        var currLat : Double
        var currLong : Double
        Log.i("","in onmapready $map")
        location.observe(viewLifecycleOwner, Observer {
            setLocationOnMap()
        })


        map.setOnCameraMoveListener(OnCameraMoveListener {
            latLng = map.cameraPosition.target
            marker?.position = latLng

        })

        map.setOnCameraIdleListener(OnCameraIdleListener {
            latLng = map.getCameraPosition().target
            currLat = latLng.latitude
            currLong = latLng.longitude
            Log.e("", "currLat: $currLat")
            Log.e("", "currLong: $currLong")
            marker?.position = latLng
        })
    }

}


