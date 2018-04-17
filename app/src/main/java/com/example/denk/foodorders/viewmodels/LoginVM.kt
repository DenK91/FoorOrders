package com.example.denk.foodorders.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import android.text.TextUtils
import com.apollographql.apollo.ApolloMutationCall
import com.example.denk.foodorders.UserMutation
import com.example.denk.foodorders.apolloClient
import com.example.denk.foodorders.data.User
import com.example.denk.foodorders.enqueue
import com.example.denk.foodorders.prefs


class LoginVM : ViewModel() {

    private var callGetUserId: ApolloMutationCall<UserMutation.Data>? = null

    private var user: MutableLiveData<User>? = null
    fun getUser(): LiveData<User> {
        if (user == null) {
            user = MutableLiveData()
            loadUsers()
        }
        return user as MutableLiveData<User>
    }

    private fun loadUsers() {
        user!!.postValue(User(prefs.userId, prefs.userFirstName, prefs.userLastName))
    }

    fun logout() {
        prefs.logout()
    }

    fun login(firstName: String, lastName:String) {
        callGetUserId?.cancel()
        callGetUserId = apolloClient.mutate(UserMutation.builder()
                .aFirstName(firstName)
                .aLastName(lastName)
                .build())
        callGetUserId?.enqueue({
            val userId = it.data()?.user()!!._id()
            if (!TextUtils.isEmpty(userId)) {
                prefs.userId = userId
                prefs.userFirstName = it.data()?.user()!!.first_name() ?: ""
                prefs.userLastName = it.data()?.user()!!.last_name() ?: ""
                loadUsers()
            }
        }, {

        })
    }

    override fun onCleared() {
        callGetUserId?.cancel()
    }
}