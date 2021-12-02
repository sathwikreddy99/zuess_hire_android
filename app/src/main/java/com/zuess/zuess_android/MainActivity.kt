package com.zuess.zuess_android


import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import viewmodels.searchViewModel


class MainApplication() : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //mobile ads initialization
        MobileAds.initialize(this){}

        //disabling action bar
        supportActionBar?.hide()

        // getting algolia's api keys
        Log.i("onmainview","on create view")
        val search : searchViewModel by viewModels()
        search.getID()



    }



    override fun onBackPressed() {
        super.onBackPressed()
    }

}






