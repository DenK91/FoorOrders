package com.example.denk.foodorders.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.example.denk.foodorders.*
import com.example.denk.foodorders.data.Place


class PlacesVM : ViewModel() {

    private var callPlacesQuery: ApolloCall<PlacesQuery.Data>? = null

    private var places: MutableLiveData<List<Place>>? = null
    fun getPlaces(): LiveData<List<Place>> {
        if (places == null) {
            places = MutableLiveData()
        }
        return places as MutableLiveData<List<Place>>
    }

    fun loadPlaces() {
        callPlacesQuery?.cancel()
        callPlacesQuery = apolloClient.query(PlacesQuery.builder().build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callPlacesQuery?.enqueue({
            val newPlaces = it.data()?.places()
            if (newPlaces != null && newPlaces.isNotEmpty()) {
                places?.postValue(newPlaces.map { Place(
                        it._id(),
                        it.description() ?: "",
                        it.name(),
                        it.phone(),
                        it.site()) })
            }
        }, {
            places?.postValue(emptyList())
        })
    }

    override fun onCleared() {
        callPlacesQuery?.cancel()
    }
}