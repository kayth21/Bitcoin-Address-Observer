package com.ceaver.bao.preferences

import androidx.preference.PreferenceManager
import com.ceaver.bao.Application
import com.ceaver.bao.extensions.getBoolean

object Preferences {

    fun isSyncOnStartup() = PreferenceManager.getDefaultSharedPreferences(Application.appContext).getBoolean("preferencesSyncOnStartup")!!
    fun isNotifyOnChange() = PreferenceManager.getDefaultSharedPreferences(Application.appContext).getBoolean("preferencesNotifyOnChange")!!
    fun isLoggingEnabled() = PreferenceManager.getDefaultSharedPreferences(Application.appContext).getBoolean("preferencesLoggingEnabled")!!
}