package com.ceaver.bao.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ceaver.bao.MainActivity
import com.ceaver.bao.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()

        startMainActivity()
    }

    private fun startMainActivity() {
        Thread.sleep(1000);
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}