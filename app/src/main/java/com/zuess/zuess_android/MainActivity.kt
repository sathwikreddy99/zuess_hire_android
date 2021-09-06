package com.zuess.zuess_android


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.google.android.libraries.places.api.Places
import com.google.android.material.bottomnavigation.BottomNavigationView
import fragments.startDirections
import viewmodels.userViewModel


class MainApplication() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        var i = Intent(this, HomeActivity::class.java)
        val model : userViewModel by viewModels()

        //navigation fragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        val navController = navHostFragment.navController

        //disabling action bar
        supportActionBar?.hide()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext,"AIzaSyBopjn5GX9KKhKpcRswj38ktOfX1gS79C0")
        }

        //initial login logic, if user is logged in then homeactivit or else login fragment
//        model.userLiveData.observe(this, Observer { user->
//            if(user==null){
//                navController.navigate(startDirections.actionStart2ToLoginui())
//            }else{
//                startActivity(i)
//            }
//
//        })




    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)


    }


}






