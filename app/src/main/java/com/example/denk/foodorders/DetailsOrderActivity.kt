package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import kotlinx.android.synthetic.main.activity_details_order.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DetailsOrderActivity : AppCompatActivity(), AnkoLogger {

    companion object {
        const val ORDER_ID_KEY: String = "order_id_key"
    }

    override val loggerTag: String
        get() = "dnek"

    private var uiHandler = Handler(Looper.getMainLooper())
    private var callGetOrderById: ApolloCall<GetOrderQuery.Data>? = null
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_order)

        orderId = intent.getStringExtra(ORDER_ID_KEY)

        setupToolbar()
        rvOrder.adapter = OrderAdapter()
        rvOrder.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {
            info { it.place.decription }
        }
        etSend.setOnTouchListener{ v, event ->
            if(event.action == MotionEvent.ACTION_UP){
                if(event.rawX >= (etSend.right - etSend.compoundDrawables[2].bounds.width())) {
                    sendComment(etSend.text.toString())
                    etSend.text.clear()
                    true
                }
            }
            false
        }
        swipeRefreshLayout.setOnRefreshListener { getOrder() }
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

    private fun getOrder() {
        callGetOrderById?.cancel()
        callGetOrderById = apolloClient.query(GetOrderQuery.builder().orderId(orderId!!).build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetOrderById?.enqueue(ApolloCallback<GetOrderQuery.Data>(object : ApolloCall.Callback<GetOrderQuery.Data>() {
            override fun onResponse(response: Response<GetOrderQuery.Data>) {
                if (response.data() != null) {
                    setOrder(response.data()?.orderById)
                } else {
                    setOrder(null)
                }
            }

            override fun onFailure(e: ApolloException) {
                setOrder(null)
            }
        }, uiHandler))
    }

    private fun sendComment(text: String){
        if(!text.isEmpty()){
            apolloClient.mutate(
                    CommentMutation.builder()
                            .order(orderId!!)
                            .user(prefs.userId)
                            .text(text)
                            .build())
                    .enqueue(null)

        }
    }

    private fun setOrder(order: GetOrderQuery.OrderById?) {
        swipeRefreshLayout.isRefreshing = false
        info { "=====ORDER=====" }
        info { "$order" }
        info { "====================" }
        getAdapter().updateOrder(order!!)
    }
}
