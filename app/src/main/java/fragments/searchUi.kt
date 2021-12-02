package fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zuess.zuess_android.R
//import services.Search
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import viewmodels.*


class searchUi : Fragment() {

    val viewModel : searchViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_ui, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationId = view.findViewById<TextView>(R.id.searchPageLocationID)
        val locationIcon = view.findViewById<ImageView>(R.id.searchPageLocationIcon)
        val searchBar = view.findViewById<EditText>(R.id.searchUiSearchBar)
        val navController= findNavController()
//        val search = Search()
        val locationViewModel : locationViewModel by activityViewModels()
        val loadingDailog = loadingDailog(requireContext())
        val recyclerView = view.findViewById<RecyclerView>(R.id.searchUiRecyclerView)
        val clearText = view.findViewById<Button>(R.id.searchUiClearSearchText)
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        Log.i("search ids","${appID},${searchID}")

        if (!viewModel.searchText.isEmpty()){
            searchBar.setText(viewModel.searchText.toString())
        }

        //getting the location for search
        if (userId != null && locationViewModel.location.value == null){
            loadingDailog.showDialog()
            db.collection("users").whereEqualTo("userId",userId.toString()).get()
                .addOnSuccessListener { snapshot->
                    loadingDailog.dismissDialog()
                    if (snapshot.documents.size != 0){
                        val data = snapshot.documents[0]
                        if (data["location.lat"] != null){
                            locationViewModel.location.value = LatLng(data["location.lat"] as Double,data["location.lng"] as Double)
                        }
                    }
                }
        }

        //setting recycler view layt
        recyclerView.layoutManager = LinearLayoutManager(context)

        //bottom navbar implementation
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbarSearch)
        bottomNavbar.setupWithNavController(navController)

//        NavigationUI.setupWithNavController(bottomNavbar,navController)

        //location id on click listener
        locationId.setOnClickListener {
            navController.navigate(searchUiDirections.actionSearchUiToLocationSelectUi())
        }

        locationIcon.setOnClickListener {
            navController.navigate(fragments.searchUiDirections.actionSearchUiToLocationSelectUi())
        }

        // search edit text configuration

//        searchBar.setImeActionLabel("Search",KeyEvent.KEYCODE_ENTER)


        searchBar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button

                if (
                    event.getAction() === KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    loadingDailog.showDialog()
                    viewModel.searchText = searchBar.text.toString()
                    Log.i("kk","in on key listener if")
                    // Perform action on key press
                    if (locationViewModel.location.value == null){
                        loadingDailog.dismissDialog()
                        Toast.makeText(requireContext(),"select a location",Toast.LENGTH_LONG).show()
                    }else{
                        viewLifecycleOwner.lifecycleScope.launch {
                            Log.i("kk","in global scope launch ${searchBar.text}")
                            viewModel.search(searchBar.text.toString(),locationViewModel.location.value, requireContext())
                        }
                    }
                    return true
                }
                return false
            }
        })

        //clear text
        clearText.setOnClickListener {
            searchBar.text.clear()
            viewModel.searchText = ""
            val adapter : searchListAdapter = searchListAdapter(null,requireContext(),communication)
            recyclerView.adapter = adapter

        }

        viewModel.searchList.observe(viewLifecycleOwner, Observer {
            value ->
            if (value.isEmpty()){
                Snackbar.make(view,"No results found",1000).show()
            }
            loadingDailog.dismissDialog()
            Log.i("in searchlist observer","$value")
            val adapter : searchListAdapter = searchListAdapter(value,requireContext(),communication)
            recyclerView.adapter = adapter
        })



        //adding ads to then adview banner
        val mAdView = view.findViewById<AdView>(R.id.adViewFreelancerPage)
        val adRequest = AdRequest.Builder()
            .build()

        mAdView.loadAd(adRequest)


    }

    var communication : searchListAdapter.searchListOnClick = object : searchListAdapter.searchListOnClick{
        override fun getInfo(docId: String) {
           view?.findNavController()?.navigate(searchUiDirections.actionSearchUiToFreelancerPage(docId))
        }

    }


}