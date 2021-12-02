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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import viewmodels.locationViewModel


class locationMapsUi : Fragment(), OnMapReadyCallback{

    val locationModel : locationViewModel by activityViewModels()
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var latLng : LatLng = LatLng(0.0,0.0)



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
        if (!Places.isInitialized()) {
            Places.initialize(this.requireContext(),"AIzaSyCbZH87WuUirnlOYX2dMAvjO6WcEDFGvd4")
        }
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
        val loadingDailog = loadingDailog(requireContext())

        //back button
        val backButton = view.findViewById<ImageView>(R.id.locationMapsBackButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }


        loadingDailog.dismissDialog()
        //initializing
        this.context?.let { Places.initialize(it,getString(R.string.maps_api_key)) }
        var placesClient = this.context?.let { Places.createClient(it) }
        //initialising map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.location_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //implementing save button
        val saveButton = view.findViewById<TextView>(R.id.locationMapsDoneText)
        saveButton.setOnClickListener {
            loadingDailog.showDialog()
            locationModel.location.value = latLng
            if (userId== null ){
                Toast.makeText(requireContext(),"login to save location to database",Toast.LENGTH_LONG).show()
                loadingDailog.dismissDialog()
                Log.i("Location model","${locationModel.location.value}")
            }
            //saving the location to database
            if (userId != null && userId !=""){
                db.collection("users").whereEqualTo("userId",userId.toString()).get()
                    .addOnSuccessListener { snapshot->
                        if (locationModel.location.value != null){
                            val data = hashMapOf<String,Any>(
                                "location" to hashMapOf<String,Any>(
                                    "lat" to locationModel.location.value!!.latitude,
                                    "lng" to locationModel.location.value!!.longitude,
                                )
                            )
                            db.collection("users").document(snapshot.documents[0].id).set(data,
                                SetOptions.merge())
                                .addOnSuccessListener {
                                    loadingDailog.dismissDialog()
                                }
                        }else if (location.value != null){
                            val data = hashMapOf<String,Any>(
                                "location" to hashMapOf<String,Any>(
                                    "lat" to location.value!!.latitude,
                                    "lng" to location.value!!.longitude,
                                )
                            )
                            db.collection("users").document(snapshot.documents[0].id).set(data,
                                SetOptions.merge())
                                .addOnSuccessListener {
                                    loadingDailog.dismissDialog()
                                }
                        }

                    }
            }
            requireActivity().onBackPressed()
        }

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
                Log.i("","asking denied permission")
                Snackbar.make(view,"enable location services",50000)
                    .setAction("enable",
                         View.OnClickListener{
                            requestPermission()
                        }
                    )
                    .show()
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
                    //passing data to location viewmodel
                    locationModel.location.value = LatLng(result.latitude,result.longitude)
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


    //setting up my location when map i ready
    @SuppressLint("MissingPermission")
    fun setLocationOnMap(){
        Log.i("","map variable $map, $location")
        if(location != null){
            var marker1 : LatLng? = location.value?.let { LatLng(it.latitude,it.longitude) }
            latLng = location.value?.let { LatLng(it.latitude,it.longitude) }!!
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

    //setting the map to previously chosen location
    @SuppressLint("MissingPermission")
    fun previousChosenLocation(){
        Log.i("","map variable $map, $location")
        if(locationModel.location.value != null){
            var marker1 : LatLng? = locationModel.location.value?.let { LatLng(it.latitude,it.longitude) }
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
        var currLat : Double
        var currLong : Double
//        loadingDailog.dismissDialog()

//        loadingDailog.showDialog()
        if (userId == null){
            Log.i("setting LonM","${locationModel.location.value}")
            if (locationModel.location.value == null){
                location.observe(viewLifecycleOwner, Observer {
                    setLocationOnMap()
                })
            }else{
                previousChosenLocation()
            }

        }else{
            db.collection("users").whereEqualTo("userId",userId).get()
                .addOnSuccessListener {
                        snapshot->
//                loadingDailog.dismissDialog()
                    Log.i("on map ready","")
                    val data = snapshot.documents[0]
                    Log.i("user location","${data["location.lat"]}")
                    if (data["location.lat"] != "" && data["location.lat"] != null){
                        locationModel.location.value = LatLng(data["location.lat"] as Double,
                            data["location.lng"] as Double
                        )
                        previousChosenLocation()
                    }else{
                        Log.i("location mvalue 1 else","${location.value}")
                        location.observe(viewLifecycleOwner, Observer {
                            setLocationOnMap()
                        })
                    }
                }
        }


//        else if (locationModel.location.value != null){
//            Log.i("location mvalue 1 in if","${locationModel.location.value}")
//
//            previousChosenLocation()
//        }

        map.setOnCameraMoveListener(OnCameraMoveListener {
            latLng = map.cameraPosition.target
            marker?.position = latLng


        })

        map.setOnCameraIdleListener(OnCameraIdleListener {
            latLng = map.getCameraPosition().target
            Log.i("","********************$latLng")
            currLat = latLng.latitude
            currLong = latLng.longitude
            Log.e("", "currLat: $currLat")
            Log.e("", "currLong: $currLong")
            marker?.position = latLng
            //passing data to location viewmodel
//            locationModel.location.value = latLng
//            Log.i("location model values:","${locationModel.location.value}")
        })
    }

    //callback when user chooses the permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i("","in onrequestpermission function")
        if(requestCode == 34){
            if(grantResults.isEmpty()){
                Log.i("", "User interaction was cancelled.")

            }
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED&&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                getLocation()
                setLocationOnMap()
            }else{
                view?.let { Snackbar.make(it,"enable location services",Snackbar.LENGTH_LONG)
                    .setAction("settings",View.OnClickListener {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(intent)
                    })
                    .show()
                }

            }
        }
    }

}


