package com.zuess.zuess_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        startActivity(Intent(baseContext, MainApplication::class.java))
        finish()
    }
}