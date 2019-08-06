package com.ceaver.bao.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ceaver.bao.MainActivity
import com.ceaver.bao.R
import com.ceaver.bao.threading.BackgroundThreadExecutor

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.splash_activity)

        BackgroundThreadExecutor.execute { startMainActivity() }
    }

    private fun startMainActivity() {
        Thread.sleep(1000);
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}