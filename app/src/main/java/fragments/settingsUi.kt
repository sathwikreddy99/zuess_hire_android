package fragments

import Authentication
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zuess.zuess_android.R
import viewmodels.locationViewModel


class settingsUi : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = view.findViewById<Button>(R.id.settingsUiLoginButton)
        val signOutButton = view.findViewById<Button>(R.id.settingsUiSignOutButton)
        val user = FirebaseAuth.getInstance().currentUser
        val locationModel : locationViewModel by activityViewModels()


        //nav controller
        val navController = findNavController()

        //settings list view
        val listView = view.findViewById<ListView>(R.id.settings_listview)
        var listItems  = arrayOf<String>("My profile","FAQs and info.")
        listView.adapter= this.context?.let { ArrayAdapter<String>(it,R.layout.settings_list_view_item,R.id.textView7,listItems) }
        listView.isClickable=  true

        listView.onItemClickListener = AdapterView.OnItemClickListener(){parent, view, position, id ->
            if (position == 0){
                if (user == null){
                    Toast.makeText(requireContext(),"You need to Login",Toast.LENGTH_LONG).show()
                }else{
                    navController.navigate(settingsUiDirections.actionSettingsUiToProfilePageUi2())
                }
            }else if(position == 1){
                navController.navigate(settingsUiDirections.actionSettingsUiToFaqUi())
            }

        }


        //bottom navigation bar settings
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbarSettings)
//        NavigationUI.setupWithNavController(bottomNavbar,navController)
        bottomNavbar.setupWithNavController(navController)

        //this is to implement back press for bottom navigation view
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    // Do custom work here
                    bottomNavbar.selectedItemId = R.id.searchUi
                    // if you want onBackPressed() to be called as normal afterwards
//                    if (isEnabled) {
//                        isEnabled = false
//                        requireActivity().onBackPressed()
//                    }
                }
            })

        //my freelancing profile button implementation
        val myFreelanceProfile = view.findViewById<Button>(R.id.settingPageFreelancerButton)
        val db = FirebaseFirestore.getInstance()
        val loadingDialog : loadingDailog = loadingDailog(requireContext())
        myFreelanceProfile.setOnClickListener {
            loadingDialog.showDialog()
            if(user == null){
                loadingDialog.dismissDialog()
                Snackbar.make(view,"You need to login",5000)
                    .setAction("Login"){
                        navController.navigate(settingsUiDirections.actionSettingsUiToLoginui())
                    }
                    .show()
            }else{
                db.collection("freelancers").whereEqualTo("uid",user.uid).get()
                    .addOnSuccessListener {
                        snapshot->
                        if (snapshot.documents.size == 0){
                            navController.navigate(settingsUiDirections.actionSettingsUiToFreelancerEdit())
//                            loadingDialog.dismissDialog()
                        }else{
                            navController.navigate(settingsUiDirections.actionSettingsUiToMyFreelancingPage2(snapshot.documents.get(0).id))
                            loadingDialog.dismissDialog()
                        }
                    }.addOnFailureListener{
//                        loadingDialog.dismissDialog()
                        Snackbar.make(view,"Error try again",2000)
                            .show()
                    }
            }

        }

        // setting visibilty of login and signout button
        if (user == null){
            signOutButton.visibility = View.GONE
        }else{
            loginButton.visibility = View.GONE
        }

        signOutButton.setOnClickListener {
            val auth : Authentication = Authentication()
            auth.signOut()
            locationModel.location.value = null
            navController.navigate(settingsUiDirections.actionSettingsUiToSearchUi())
        }

        loginButton.setOnClickListener {
            navController.navigate(settingsUiDirections.actionSettingsUiToLoginui())
        }


//        fun addDatafun(){
//
//            for(i in 0..4){
//                var arjun = hashMapOf(
//                    "name" to "arjun reddy$i",
//                    "job_titles" to hashMapOf(
//                        "0" to "surgeon",
//                        "1" to "doctor",
//                        "2" to "cardiologist"),
//                    "services" to hashMapOf(
//                        "0" to hashMapOf(
//                            "name" to "surgeries",
//                            "price" to 2000
//                        ),
//                        "1" to hashMapOf(
//                            "name" to "consultancy",
//                            "price" to 3000
//                        ),
//                        "2" to hashMapOf(
//                            "type" to "nutrition",
//                            "price" to 1500
//                        ),
//                        "3" to hashMapOf(
//                            "name" to "home checkup",
//                            "price" to 4500
//                        )
//                    )
//
//
//
//                )
//
//                db.collection("freelancers").add(arjun)
//
//            }
//
//        }




    }

}