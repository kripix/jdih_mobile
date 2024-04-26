package com.krispy.kelompok1_jdih.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        private const val PREF_NAME = "MyAppPrefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username" // Optional: Store username if needed
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false) // Default to false if not set
    }

    fun setUsername(username: String) { // Optional: Store username if needed
        editor.putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? { // Optional: Retrieve username if needed
        return pref.getString(KEY_USERNAME, null)
    }

    fun clear() {
        editor.clear().apply()
    }
}