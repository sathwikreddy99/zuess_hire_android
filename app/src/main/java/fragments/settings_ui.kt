package fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zuess.zuess_android.HomeActivity
import com.zuess.zuess_android.R
import com.zuess.zuess_android.SearchActivity
import com.zuess.zuess_android.settingsActivity


class settings_ui : Fragment() {


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
                navController.navigate(settings_uiDirections.actionSettingsToProfilePageUi())
            }
            
        }


        //bottom navigation bar settings
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbarSettings)
        bottomNavbar.selectedItemId = R.id.settingsIcon

        if (bottomNavbar != null) {
            bottomNavbar.setOnItemSelectedListener { item->
                when(item.itemId){
                    R.id.homeIcon->{
                        startActivity(Intent(activity, HomeActivity::class.java))
                        true
                    }
                    R.id.searchIcon->{
                        startActivity(Intent(activity, SearchActivity::class.java))
                        true
                    }
                    R.id.settingsIcon->{
                        startActivity(Intent(activity, settingsActivity::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
    }

}