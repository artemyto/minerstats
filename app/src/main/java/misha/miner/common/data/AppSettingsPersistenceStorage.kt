package misha.miner.common.data

import android.content.SharedPreferences
import androidx.core.content.edit

class AppSettingsPersistenceStorage(private val preferences: SharedPreferences) {

    companion object {
        const val SAVED_SCAN_IP = "saved_scan_ip"
    }

    fun writeString(key: String, value: String) = preferences.edit { putString(key, value) }

    fun readString(key: String, default: String = "") = preferences.getString(key, default)
}
