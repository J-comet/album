package hs.project.album.util

import android.content.Context
import android.content.SharedPreferences
import hs.project.album.BaseActivity
import hs.project.album.MyApplication
import hs.project.album.R

class PreferenceUtil(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(context.resources.getString(R.string.app_name), Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return prefs.getBoolean(key, defValue)
    }

    fun setBoolean(key: String, boolean: Boolean) {
        prefs.edit().putBoolean(key, boolean).apply()
    }

}