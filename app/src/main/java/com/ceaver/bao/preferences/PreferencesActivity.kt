package com.ceaver.bao.preferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.work.*
import com.ceaver.bao.Application
import com.ceaver.bao.R
import com.ceaver.bao.extensions.getBoolean
import com.ceaver.bao.extensions.getEnabledOrDisabledText
import com.ceaver.bao.extensions.getString
import com.ceaver.bao.logging.LogCategory
import com.ceaver.bao.logging.LogRepository
import com.ceaver.bao.worker.Workers

const val BACKGROUND_PROCESS_ID = "com.ceaver.bao.preferences.PreferencesActivity.periodicBackgroundProcessId"

class PreferencesActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preferences_activity)
        supportFragmentManager.beginTransaction().replace(R.id.preferences, PreferencesFragment()).commit()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            "preferencesSyncInBackground" -> onSyncInBackgroundChanged(BackgroundSyncInterval.valueOf(sharedPreferences.getString(key)!!))
        }
        if(Preferences.isLoggingEnabled() || key == "preferencesLoggingEnabled") {
            val message = when (key) {
                "preferencesSyncOnStartup" -> "Sync on startup ${sharedPreferences.getBoolean(key)!!.getEnabledOrDisabledText()}"
                "preferencesSyncInBackground" -> "Sync in background changed to ${BackgroundSyncInterval.valueOf(sharedPreferences.getString(key)!!).toHumanReadableString()}"
                "preferencesNotifyOnChange" -> "Notify on address change ${sharedPreferences.getBoolean(key)!!.getEnabledOrDisabledText()}"
                "preferencesLoggingEnabled" -> "Logging ${sharedPreferences.getBoolean(key)!!.getEnabledOrDisabledText()}"
                else -> throw IllegalStateException()
            }
            LogRepository.insertLogAsync(message, LogCategory.SETTINGS)
        }
    }

    private fun onSyncInBackgroundChanged(backgroundSyncInterval: BackgroundSyncInterval) {
        if (backgroundSyncInterval == BackgroundSyncInterval.NONE) {
            WorkManager.getInstance().cancelUniqueWork(BACKGROUND_PROCESS_ID)
        } else {
            val repeatInterval = backgroundSyncInterval.repeatInterval!!
            val repeatIntervalTimeUnit = backgroundSyncInterval.repeatIntervalTimeUnit!!
            val flexTimeInterval: Long = backgroundSyncInterval.flexTimeInterval!!
            val flexTimeIntervalUnit = backgroundSyncInterval.flexTimeIntervalUnit!!
            val backgroundProcess = PeriodicWorkRequestBuilder<StartWorker>(repeatInterval, repeatIntervalTimeUnit, flexTimeInterval, flexTimeIntervalUnit).build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(BACKGROUND_PROCESS_ID, ExistingPeriodicWorkPolicy.REPLACE, backgroundProcess)
        }
    }

    class StartWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            Workers.run()
            return Result.success()
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(Application.appContext).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(Application.appContext).unregisterOnSharedPreferenceChangeListener(this)
    }

    class PreferencesFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}