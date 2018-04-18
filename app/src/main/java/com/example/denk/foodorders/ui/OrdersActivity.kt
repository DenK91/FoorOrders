package com.example.denk.foodorders.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_orders.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.support.v7.app.AlertDialog
import com.example.denk.foodorders.*
import com.example.denk.foodorders.adapters.OrdersAdapter
import com.example.denk.foodorders.adapters.PlaceAdapter
import com.example.denk.foodorders.data.Order
import com.example.denk.foodorders.viewmodels.LoginVM
import com.example.denk.foodorders.viewmodels.OrdersVM
import com.example.denk.foodorders.viewmodels.PlacesVM
import org.jetbrains.anko.toast

class OrdersActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    private var loginVM: LoginVM? = null
    private var placesVM: PlacesVM? = null
    private var ordersVM: OrdersVM? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        loginVM = ViewModelProviders.of(this).get(LoginVM::class.java)
        placesVM = ViewModelProviders.of(this).get(PlacesVM::class.java)
        ordersVM = ViewModelProviders.of(this).get(OrdersVM::class.java)
        setupToolbar()
        rvOrders.adapter = OrdersAdapter()
        rvOrders.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {
            info { it.place.description }
            startActivity(intentFor<DetailsOrderActivity>()
                    .putExtra(DetailsOrderActivity.ORDER_ID_KEY, it.id))
        }
        swipeRefreshLayout.setOnRefreshListener { ordersVM?.loadOrders() }
        btnAddOrder.onClick { placesVM?.loadPlaces() }
        initPlacesObserver()
        ordersVM?.getOrders()?.observe(this, Observer { setOrders(it ?: emptyList()) })
    }

    private fun getAdapter(): OrdersAdapter = rvOrders.adapter as OrdersAdapter

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
                loginVM?.logout()
                startActivity(intentFor<LoginActivity>())
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setOrders(orders: List<Order>) {
        swipeRefreshLayout.isRefreshing = false
        info { "=====ORDES LIST=====" }
        orders.forEach {
            info { "${it.place.description} ${it.user.firstName} ${it.user.lastName}" }
        }
        info { "====================" }
        getAdapter().updateOrders(orders)
    }

    private fun initPlacesObserver() {
        placesVM?.getPlaces()?.observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                val builderSingle = AlertDialog.Builder(this@OrdersActivity)
                builderSingle.setIcon(R.mipmap.ic_launcher)
                builderSingle.setTitle("Где заказывать?")

                val arrayAdapter = PlaceAdapter(this@OrdersActivity, it.toMutableList())

                builderSingle.setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })
                builderSingle.setAdapter(arrayAdapter, { _, which ->
                    val place = arrayAdapter.getItem(which)
                    val builderInner = AlertDialog.Builder(this@OrdersActivity)
                    builderInner.setMessage("${place.name}\n${place.description}")
                    builderInner.setTitle("Вы выбрали:")
                    builderInner.setPositiveButton("Создать", { dialog, _ ->
                        run {
                            ordersVM?.createOrder(place)?.observe(this@OrdersActivity, Observer {
                                dialog.dismiss()
                                if (it != null && it.id.isNotEmpty()) {
                                    startActivity(intentFor<DetailsOrderActivity>()
                                            .putExtra(DetailsOrderActivity.ORDER_ID_KEY, it.id))
                                    toast("Заказ создан успешно!")
                                } else {
                                    toast("Заказ не создан!")
                                }
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
