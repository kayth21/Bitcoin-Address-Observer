package com.ceaver.bao.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.ceaver.bao.MainActivity
import com.ceaver.bao.R
import com.ceaver.bao.preferences.Preferences
import com.ceaver.bao.threading.BackgroundThreadExecutor
import com.ceaver.bao.worker.Workers

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        initDefaultPreferences()
    }

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.splash_activity)

        BackgroundThreadExecutor.execute { 
            startMainActivity()
            if (Preferences.isSyncOnStartup())
                Workers.run()
        }
    }

    private fun initDefaultPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
    }

    private fun startMainActivity() {
        Thread.sleep(1000);
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}