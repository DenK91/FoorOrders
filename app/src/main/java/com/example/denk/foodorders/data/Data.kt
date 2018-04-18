package com.example.denk.foodorders.data

import android.text.TextUtils

data class User(val id: String,
                var firstName: String? = "",
                var lastName: String? = "") {

    fun isLogin(): Boolean = !TextUtils.isEmpty(id)
}

data class Product(val id: String,
                   val name: String,
                   val description: String,
                   val price: Long,
                   var photo: String? = null)

data class Place(val id: String,
                 val description: String,
                 val name: String,
                 val phone: String? = null,
                 val site: String? = null,
                 var products: List<Product>? = null,
                 var delivery_cost: Long? = null,
                 var delivery_limit: Long? = null)

data class Comment(val id: String,
                   val user: User,
                   val text: String)

data class Order(val id: String,
                 val user: User,
                 val place: Place,
                 var date: Long? = null,
                 var state: State = State.ACTIVE,
                 var subOrders: List<SubOrder>? = null,
                 var comments: List<Comment>? = null)

data class SubOrder(val id: String,
                    val user: User,
                    var products: List<Product>? = null,
                    var comment: String? = null)

enum class State {
    ACTIVE,
    PURCHASED,
    ARCHIVED
}