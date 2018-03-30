package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.example.denk.foodorders.adapters.FinishProductsAdapter
import com.example.denk.foodorders.type.State
import kotlinx.android.synthetic.main.activity_finish_order.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk25.coroutines.onClick

class FinishOrderActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    companion object {
        const val ORDER_ID_KEY: String = "order_id_key"
    }

    private var callGetOrderById: ApolloCall<OrderQuery.Data>? = null
    private var order: OrderQuery.Order? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_order)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        rvOrder.adapter = FinishProductsAdapter()
        rvOrder.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayout.setOnRefreshListener { getOrder() }
        btnFinishOrder.onClick {
            apolloClient.mutate(EditStateMutation(getOrderId(), State.PURCHASED)).enqueue({finish()})
        }
    }

    override fun onResume() {
        super.onResume()
        getOrder()
    }

    private fun getAdapter(): FinishProductsAdapter = rvOrder.adapter as FinishProductsAdapter

    private fun getOrderId() : String = order?._id ?: intent.getStringExtra(DetailsOrderActivity.ORDER_ID_KEY)

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getOrder() {
        callGetOrderById?.cancel()
        callGetOrderById = apolloClient.query(OrderQuery.builder().orderId(getOrderId()).build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetOrderById?.enqueue({ setOrder(it.data()?.order) })
    }

    private fun setOrder(order: OrderQuery.Order?) {
        swipeRefreshLayout.isRefreshing = false
        this.order = order
        val allProductsInOrder = order?.subOrders?.flatMap { it.products?.toList()!! }
                ?.groupBy { it._id() }?.map { it.value }?.toMutableList()!!
        getAdapter().data = allProductsInOrder
        var sum : Long = 0
        allProductsInOrder.forEach { it.forEach { sum +=it.price } }
        btnFinishOrder.text = "Заказать на сумму $sum"
    }
}
