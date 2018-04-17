package com.example.denk.foodorders.data

import android.text.TextUtils

data class User(var id: String,
                var firstName: String,
                var lastName: String) {

    fun isLogin(): Boolean = !TextUtils.isEmpty(id)
}