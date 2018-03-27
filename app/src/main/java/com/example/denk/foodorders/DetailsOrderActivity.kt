package com.example.denk.foodorders

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import kotlinx.android.synthetic.main.activity_details_order.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor

class DetailsOrderActivity : AppCompatActivity(), AnkoLogger {

    companion object {
        const val ORDER_ID_KEY: String = "order_id_key"
    }

    override val loggerTag: String
        get() = "dnek"

    private var uiHandler = Handler(Looper.getMainLooper())
    private var callGetOrderById: ApolloCall<OrderQuery.Data>? = null
    private var orderId: String? = null
    private lateinit var placeId: String

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details_order, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.create_suborder ->
                startActivity(intentFor<CreateSuborderActivity>()
                        .putExtra(CreateSuborderActivity.PLACE_ID, placeId))

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
        etSend.setOnTouchListener{ _, event ->
            if(event.action == MotionEvent.ACTION_UP){
                if(event.rawX >= (etSend.right - etSend.compoundDrawables[2].bounds.width())) {
                    sendComment(etSend.text.toString())
                    etSend.text.clear()
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

    private fun getOrder(scrollToEnd: Boolean = false) {
        callGetOrderById?.cancel()
        callGetOrderById = apolloClient.query(OrderQuery.builder().orderId(orderId!!).build())
                .responseFetcher(ApolloResponseFetchers.NETWORK_FIRST)
        callGetOrderById?.enqueue(ApolloCallback<OrderQuery.Data>(object : ApolloCall.Callback<OrderQuery.Data>() {
            override fun onResponse(response: Response<OrderQuery.Data>) {
                if (response.data() != null) {
                    setOrder(response.data()?.order, scrollToEnd)
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
                    .enqueue({
                        getOrder(true)
                    })
        }
    }

    private fun setOrder(order: OrderQuery.Order?, scrollToEnd : Boolean = false) {
        swipeRefreshLayout.isRefreshing = false
        info { "=====ORDER=====" }
        info { "$order" }
        info { "====================" }
        placeId = order!!.place()._id
        getAdapter().updateOrder(order)
        if (scrollToEnd) {
            rvOrder.scrollToPosition(getAdapter().itemCount - 1)
        }
    }
}
