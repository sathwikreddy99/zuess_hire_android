package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zuess.zuess_android.R

class searchUi : Fragment() {


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

        val navController= findNavController()
        //bottom navbar implementation
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbarSearch)

        NavigationUI.setupWithNavController(bottomNavbar,navController)

//        bottomNavbar.selectedItemId = R.id.searchIcon
//        val navController = findNavController()
//
//        if (bottomNavbar != null) {
//            bottomNavbar.setOnItemSelectedListener { item->
//                when(item.itemId){
//                    R.id.homeIcon->{
//                        navController.navigate(searchUiDirections.actionSearchUiToHomeUi())
//                        true
//                    }
//                    R.id.searchIcon->{
//                        true
//                    }
//                    R.id.settingsIcon->{
//                        navController.navigate(searchUiDirections.actionSearchUiToSettingsUi())
//                        true
//                    }
//                    R.id.chatIcon->{
//                        navController.navigate(searchUiDirections.actionSearchUiToChatsListUi())
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
    }

}