package fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zuess.zuess_android.HomeActivity
import com.zuess.zuess_android.R
import com.zuess.zuess_android.SearchActivity
import com.zuess.zuess_android.settingsActivity

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
        //bottom navbar implementation
        val bottomNavbar = view.findViewById<BottomNavigationView>(R.id.bottomNavbarSearch)
        bottomNavbar.selectedItemId = R.id.searchIcon

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