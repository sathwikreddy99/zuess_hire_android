package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zuess.zuess_android.R


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


        //nav controller
        val navController = findNavController()

        //settings list view
        val listView = view.findViewById<ListView>(R.id.settings_listview)
        var listItems  = arrayOf<String>("My profile","My addresses","My hirings","FAQs","Policies")
        listView.adapter= this.context?.let { ArrayAdapter<String>(it,R.layout.settings_list_view_item,R.id.textView7,listItems) }
        listView.isClickable=  true

        listView.onItemClickListener = AdapterView.OnItemClickListener(){parent, view, position, id ->
            if (position == 0){
                navController.navigate(settingsUiDirections.actionSettingsUiToProfilePageUi2())
            }
            
        }


        //bottom navigation bar settings
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbarSettings)
        NavigationUI.setupWithNavController(bottomNavbar,navController)

//        bottomNavbar.selectedItemId = R.id.settingsIcon
//
//        if (bottomNavbar != null) {
//            bottomNavbar.setOnItemSelectedListener { item->
//                when(item.itemId){
//                    R.id.homeIcon->{
//                        navController.navigate(settingsUiDirections.actionSettingsUiToHomeUi())
//                        true
//                    }
//                    R.id.searchIcon->{
//                        navController.navigate(settingsUiDirections.actionSettingsUiToSearchUi())
//                        true
//                    }
//                    R.id.settingsIcon->{
//                        false
//                    }
//                    R.id.chatIcon->{
//                        navController.navigate(settingsUiDirections.actionSettingsUiToChatsListUi())
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
    }

}