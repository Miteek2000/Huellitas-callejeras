package com.proyecto.huellitas_callejeras.data.auth

import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_AUTH_TOKEN, value).apply()
        }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "HuellitasAppPrefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
