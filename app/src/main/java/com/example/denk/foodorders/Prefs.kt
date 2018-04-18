package com.example.denk.foodorders

import android.content.Context
import android.content.SharedPreferences
import com.example.denk.foodorders.data.User

class Prefs (context: Context) {
    private val PREFS_FILENAME = "com.example.denk.foodorders.prefs"
    private val USER_ID = "user_id"
    private val USER_FIRST_NAME = "user_first_name"
    private val USER_LAST_NAME = "user_last_name"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var userId: String
        get() = prefs.getString(USER_ID, "")
        set(value) = prefs.edit().putString(USER_ID, value).apply()

    var userFirstName: String
        get() = prefs.getString(USER_FIRST_NAME, "")
        set(value) = prefs.edit().putString(USER_FIRST_NAME, value).apply()

    var userLastName: String
        get() = prefs.getString(USER_LAST_NAME, "")
        set(value) = prefs.edit().putString(USER_LAST_NAME, value).apply()

    var user: User
        get() = User(userId, userFirstName, userLastName)
        set(value) {
            userId = value.id
            userFirstName = value.firstName ?: ""
            userLastName = value.lastName ?: ""
        }

    fun logout() {
        userId = ""
        userFirstName = ""
        userLastName = ""
    }
}