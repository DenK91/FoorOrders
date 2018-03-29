package com.example.denk.foodorders

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.example.denk.foodorders.adapters.OrderAdapter
import kotlinx.android.synthetic.main.activity_details_order.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import java.util.ArrayList

class DetailsOrderActivity : AppCompatActivity(), AnkoLogger {

    companion object {
        const val ORDER_ID_KEY: String = "order_id_key"
    }

    override val loggerTag: String
        get() = "dnek"

    private var callGetOrderById: ApolloCall<OrderQuery.Data>? = null
    private var orderId: String? = null
    private lateinit var placeId: String
    private var optionsMenu : Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        optionsMenu = menu
        menuInflater.inflate(R.menu.menu_details_order, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.create_suborder ->
                startActivity(intentFor<CreateSuborderActivity>()
                        .putExtra(CreateSuborderActivity.PLACE_ID, placeId)
                        .putExtra(CreateSuborderActivity.ORDER_ID, orderId))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_order)

        orderId = intent.getStringExtra(ORDER_ID_KEY)
        info { orderId }

        setupToolbar()
        rvOrder.adapter = OrderAdapter()
        rvOrder.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {

            info { it.place.description }
        }
        getAdapter().subOrderClickedListen {
            if (it.user._id == prefs.userId) {
                val products : ArrayList<String> = ArrayList()
                it.products?.forEach { products.add(it._id) }
                startActivity(intentFor<SubOrderActivity>()
                        .putExtra(SubOrderActivity.PLACE_ID, placeId)
                        .putExtra(SubOrderActivity.ORDER_ID, orderId)
                        .putExtra(SubOrderActivity.SUB_ORDER_ID, it._id)
                        .putExtra(SubOrderActivity.PRODUCTS_ID, products))
            }
        }
        etSend.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (etSend.right - etSend.compoundDrawables[2].bounds.width())) {
                    sendComment(etSend.text.toString())
                    etSend.text.clear()
                }
            }
            false
        }
        swipeRefreshLayout.setOnRefreshListener { getOrder() }
    }

    override fun onResume() {
        super.onResume()
        getOrder()
    }

    private fun getAdapter(): OrderAdapter = rvOrder.adapter as OrderAdapter

    private fun setupToolbar() {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getOrder(scrollToEnd: Boolean = false) {
        callGetOrderById?.cancel()
        callGetOrderById = apolloClient.query(OrderQuery.builder().orderId(orderId!!).build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetOrderById?.enqueue({ setOrder(it.data()?.order, scrollToEnd) })
    }

    private fun sendComment(text: String) {
        if (!text.isEmpty()) {
            apolloClient.mutate(CommentMutation(prefs.userId, orderId!!, text))
                    .enqueue({getOrder(true)})
        }
    }

    private fun setOrder(order: OrderQuery.Order?, scrollToEnd: Boolean = false) {
        swipeRefreshLayout.isRefreshing = false
        info { "=====ORDER=====" }
        info { "$order" }
        info { "====================" }
        placeId = order!!.place()._id
        getAdapter().updateOrder(order)
        if (scrollToEnd) {
            rvOrder.scrollToPosition(getAdapter().itemCount - 1)
        }
        optionsMenu?.findItem(R.id.create_suborder)?.isVisible = order.subOrders?.none { prefs.userId == it.user._id }!!
    }
}
