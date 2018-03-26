package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import kotlinx.android.synthetic.main.activity_orders.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor

class OrdersActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    private var uiHandler = Handler(Looper.getMainLooper())
    private var callGetListOrders: ApolloCall<GetListOrdersQuery.Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        setupToolbar()
        rvOrders.adapter = OrdersAdapter()
        rvOrders.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {
            info { it.place.decription }
            startActivity(intentFor<DetailsOrderActivity>()
                    .putExtra(DetailsOrderActivity.ORDER_ID_KEY, it._id))
        }
        swipeRefreshLayout.setOnRefreshListener { getOrders() }
    }

    override fun onResume() {
        super.onResume()
        getOrders()
    }

    private fun getAdapter(): OrdersAdapter = rvOrders.adapter as OrdersAdapter

    override fun onDestroy() {
        super.onDestroy()
        callGetListOrders?.cancel()
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu1, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                prefs.userId = ""
                startActivity(intentFor<MainActivity>())
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getOrders() {
        callGetListOrders?.cancel()
        callGetListOrders = apolloClient.query(GetListOrdersQuery.builder().build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetListOrders?.enqueue(ApolloCallback<GetListOrdersQuery.Data>(object : ApolloCall.Callback<GetListOrdersQuery.Data>() {
            override fun onResponse(response: Response<GetListOrdersQuery.Data>) {
                if (response.data() != null) {
                    setOrders(response.data()!!.orders)
                } else {
                    setOrders(null)
                }
            }

            override fun onFailure(e: ApolloException) {
                setOrders(null)
            }
        }, uiHandler))
    }

    private fun setOrders(orders: List<GetListOrdersQuery.Order>?) {
        swipeRefreshLayout.isRefreshing = false
        info { "=====ORDES LIST=====" }
        orders?.forEach {
            info { "${it.place.decription} ${it.admin.first_name} ${it.admin.last_name}" }
        }
        info { "====================" }
        getAdapter().updateOrders(orders)
    }
}
