package com.example.denk.foodorders

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils

class Prefs (context: Context) {
    private val PREFS_FILENAME = "com.example.denk.foodorders.prefs"
    private val USER_ID = "user_id"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var userId: String
        get() = prefs.getString(USER_ID, "")
        set(value) = prefs.edit().putString(USER_ID, value).apply()

    fun isLogin():Boolean = !TextUtils.isEmpty(userId)
}