package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import kotlinx.android.synthetic.main.activity_orders.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.support.v7.app.AlertDialog
import com.example.denk.foodorders.adapters.OrdersAdapter
import com.example.denk.foodorders.adapters.PlaceAdapter
import org.jetbrains.anko.toast

class OrdersActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    private var callGetListOrders: ApolloCall<OrdersQuery.Data>? = null
    private var callPlacesQuery: ApolloCall<PlacesQuery.Data>? = null
    private var callCreateOrderMutation: ApolloCall<CreateOrderMutation.Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        setupToolbar()
        rvOrders.adapter = OrdersAdapter()
        rvOrders.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {
            info { it.place.description }
            startActivity(intentFor<DetailsOrderActivity>()
                    .putExtra(DetailsOrderActivity.ORDER_ID_KEY, it._id))
        }
        swipeRefreshLayout.setOnRefreshListener { getOrders() }
        btnAddOrder.onClick { showPlaces() }
    }

    override fun onResume() {
        super.onResume()
        getOrders()
    }

    private fun getAdapter(): OrdersAdapter = rvOrders.adapter as OrdersAdapter

    override fun onDestroy() {
        super.onDestroy()
        callGetListOrders?.cancel()
        callPlacesQuery?.cancel()
        callCreateOrderMutation?.cancel()
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
        callGetListOrders = apolloClient.query(OrdersQuery.builder().build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetListOrders?.enqueue({
            if (it.data() != null) {
                setOrders(it.data()!!.orders)
            } else {
                setOrders(null)
            }
        }, {
            setOrders(null)
        })
    }

    private fun setOrders(orders: List<OrdersQuery.Order>?) {
        swipeRefreshLayout.isRefreshing = false
        info { "=====ORDES LIST=====" }
        orders?.forEach {
            info { "${it.place.description} ${it.user.first_name} ${it.user.last_name}" }
        }
        info { "====================" }
        getAdapter().updateOrders(orders)
    }

    private fun showPlaces() {
        callPlacesQuery?.cancel()
        callPlacesQuery = apolloClient.query(PlacesQuery.builder().build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callPlacesQuery?.enqueue({
            val places = it.data()?.places
            if (places != null && places.isNotEmpty()) {
                val builderSingle = AlertDialog.Builder(this@OrdersActivity)
                builderSingle.setIcon(R.mipmap.ic_launcher)
                builderSingle.setTitle("Где заказывать?")

                val arrayAdapter = PlaceAdapter(this@OrdersActivity, places)

                builderSingle.setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                builderSingle.setAdapter(arrayAdapter, { _, which ->
                    val place = arrayAdapter.getItem(which)
                    val builderInner = AlertDialog.Builder(this@OrdersActivity)
                    builderInner.setMessage("${place.name}\n${place.description}")
                    builderInner.setTitle("Вы выбрали:")
                    builderInner.setPositiveButton("Создать", { dialog, _ ->
                        run {
                            callCreateOrderMutation?.cancel()
                            callCreateOrderMutation = apolloClient.mutate(CreateOrderMutation.builder()
                                    .userId(prefs.userId).placeId(place._id).build())
                            callCreateOrderMutation?.enqueue({
                                dialog.dismiss()
                                startActivity(intentFor<DetailsOrderActivity>()
                                        .putExtra(DetailsOrderActivity.ORDER_ID_KEY, it.data()?.order!!._id))
                                toast("Заказ создан успешно!")
                            }, {
                                dialog.dismiss()
                            })
                        }
                    })
                    builderInner.show()
                })
                builderSingle.show()
            }
        })
    }
}
