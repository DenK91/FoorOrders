package com.example.denk.foodorders.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.example.denk.foodorders.*
import com.example.denk.foodorders.data.Order
import com.example.denk.foodorders.data.Place
import com.example.denk.foodorders.data.User
import com.example.denk.foodorders.data.State as DataState
import com.example.denk.foodorders.type.State as OrderState


class OrdersVM : ViewModel() {

    private var callCreateOrderMutation: ApolloCall<CreateOrderMutation.Data>? = null
    private var callGetListOrders: ApolloCall<OrdersQuery.Data>? = null

    private var orders: MutableLiveData<List<Order>>? = null
    fun getOrders(): LiveData<List<Order>> {
        if (orders == null) {
            orders = MutableLiveData()
            loadOrders()
        }
        return orders as MutableLiveData<List<Order>>
    }

    fun loadOrders() {
        callGetListOrders?.cancel()
        callGetListOrders = apolloClient.query(OrdersQuery.builder().build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetListOrders?.enqueue({
            orders?.postValue(it.data()?.orders()?.map {
                Order(it._id(),
                        User(it.user()._id(), it.user().first_name(), it.user().last_name()),
                        Place(it.place()._id(), it.place().description() ?: "", ""),
                        it.date(),
                        when(it.state()) {
                            OrderState.PURCHASED -> DataState.PURCHASED
                            OrderState.ACTIVE -> DataState.ACTIVE
                            OrderState.ARCHIVED -> DataState.ARCHIVED
                            OrderState.`$UNKNOWN` -> DataState.ACTIVE
                        })
            } ?: emptyList())
        }, {
            orders?.postValue(emptyList())
        })
    }

    fun createOrder(place:Place) : LiveData<Order> {
        val resData : MutableLiveData<Order> = MutableLiveData()
        val user = User(prefs.userId, prefs.userFirstName, prefs.userLastName)
        callCreateOrderMutation?.cancel()
        callCreateOrderMutation = apolloClient.mutate(CreateOrderMutation.builder()
                .userId(user.id).placeId(place.id).build())
        callCreateOrderMutation?.enqueue({
            resData.postValue(Order(it.data()?.order()?._id() ?: "", user, place))
        }, {
            resData.postValue(Order("", user, place))
        })
        return resData
    }

    override fun onCleared() {
        callCreateOrderMutation?.cancel()
        callGetListOrders?.cancel()
    }
}