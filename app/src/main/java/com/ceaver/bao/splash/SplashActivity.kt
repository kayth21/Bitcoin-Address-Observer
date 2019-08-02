package com.ceaver.bao.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ceaver.bao.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        supportActionBar?.hide()
    }
}