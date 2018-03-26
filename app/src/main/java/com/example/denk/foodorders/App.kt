package com.example.denk.foodorders

import android.app.Application
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import okhttp3.OkHttpClient

val prefs: Prefs by lazy {
    App.prefs!!
}

val apolloClient: ApolloClient by lazy {
    App.apolloClient!!
}

class App : Application() {

    private val BASE_URL : String = "http://192.168.88.13:8080/graphql"
    private val SUBSCRIPTION_BASE_URL = "wss://192.168.88.13:8080/subscriptions"
    private val SQL_CACHE_NAME = "apolloDB"

    companion object {
        var apolloClient : ApolloClient? = null
        var prefs: Prefs? = null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = Prefs(applicationContext)
//
//        val cacheKeyResolver = object : CacheKeyResolver() {
//            override fun fromFieldRecordSet(field: ResponseField, recordSet: Map<String, Any>): CacheKey {
//                val typeName = recordSet["__typename"] as String
//                if ("User" == typeName) {
//                    val userKey = typeName + "." + recordSet["login"]
//                    return CacheKey.from(userKey)
//                }
//                if (recordSet.containsKey("id")) {
//                    val typeNameAndIDKey = recordSet["__typename"].toString() + "." + recordSet["id"]
//                    return CacheKey.from(typeNameAndIDKey)
//                }
//                return CacheKey.NO_KEY
//            }
//            // Use this resolver to customize the key for fields with variables: eg entry(repoFullName: $repoFullName).
//            // This is useful if you want to make query to be able to resolved, even if it has never been run before.
//            override fun fromFieldArguments(field: ResponseField, variables: Operation.Variables): CacheKey {
//                return CacheKey.NO_KEY
//            }
//        }

        val okHttpClient = OkHttpClient.Builder().build()

        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .normalizedCache(LruNormalizedCacheFactory(EvictionPolicy.NO_EVICTION)
                        .chain(SqlNormalizedCacheFactory(ApolloSqlHelper(this, SQL_CACHE_NAME))))
//                        cacheKeyResolver)
                .subscriptionTransportFactory(WebSocketSubscriptionTransport.Factory(SUBSCRIPTION_BASE_URL, okHttpClient))
                .build()
    }

}