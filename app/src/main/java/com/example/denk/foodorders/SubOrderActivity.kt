package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import com.apollographql.apollo.api.Input
import com.example.denk.foodorders.adapters.MyOrderAdapter
import kotlinx.android.synthetic.main.activity_sub_order.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor

class SubOrderActivity : AppCompatActivity(), AnkoLogger {

    override val loggerTag: String
        get() = "dnek"

    companion object {
        const val PLACE_ID = "place_id"
        const val ORDER_ID = "order_id"
        const val SUB_ORDER_ID = "sub_order_id"
        const val PRODUCTS_ID = "products_id"
        const val COMMENT = "comment"
    }

    private lateinit var placeId : String
    private lateinit var orderId: String
    private lateinit var subOrderId: String
    private lateinit var allProducts : List<PlaceQuery.Product>
    private var purchaseItems = arrayListOf<String>()
    private var comment: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_order)

        placeId = intent.getStringExtra(PLACE_ID)
        orderId = intent.getStringExtra(ORDER_ID)
        subOrderId = intent.getStringExtra(SUB_ORDER_ID)
        comment = intent.getStringExtra(COMMENT)
        val products = intent.getStringArrayListExtra(PRODUCTS_ID)
        if (products != null && !products.isEmpty()) {
            purchaseItems = products
        } else {
            startActivity(intentFor<ProductListActivity>()
                    .putExtra(ProductListActivity.PLACE_ID, placeId)
                    .putExtra(ProductListActivity.ORDER_ID, orderId)
                    .putExtra(ProductListActivity.SUB_ORDER_ID, subOrderId)
                    .putExtra(ProductListActivity.COMMENT, comment))
            finish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        rvProducts.adapter = MyOrderAdapter().listenAddProduct {
            purchaseItems.add(it._id)
            apolloClient.mutate(EditSubOrderMutation(subOrderId, Input.fromNullable(comment), Input.fromNullable(purchaseItems)))
                    .enqueue({updateList()})
        }.listenRemoveProduct {
            purchaseItems.remove(it._id)
            if(purchaseItems.isNotEmpty()) {
                apolloClient.mutate(EditSubOrderMutation(subOrderId, Input.fromNullable(comment), Input.fromNullable(purchaseItems)))
                        .enqueue({updateList()})
            } else {
                apolloClient.mutate(DeleteSubOrderMutation(subOrderId)).enqueue({finish()})
            }

        }
        rvProducts.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayout.setOnRefreshListener { getPlace() }
        etSend.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (etSend.right - etSend.compoundDrawables[2].bounds.width())) {
                    comment = etSend.text.toString()
                    sendComment(comment)
                    etSend.text.clear()
                }
            }
            false
        }

        getPlace()
    }

    private fun sendComment(comment: String?) {
        apolloClient.mutate(EditSubOrderMutation(subOrderId, Input.fromNullable(comment), Input.fromNullable(purchaseItems)))
                .enqueue({ updateList()})
    }

    private fun getAdapter() = rvProducts.adapter as MyOrderAdapter

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_suborder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.products_list -> {
                startActivity(intentFor<ProductListActivity>()
                        .putExtra(ProductListActivity.PLACE_ID, placeId)
                        .putExtra(ProductListActivity.ORDER_ID, orderId)
                        .putExtra(ProductListActivity.SUB_ORDER_ID, subOrderId)
                        .putExtra(ProductListActivity.PRODUCTS_ID, purchaseItems)
                        .putExtra(ProductListActivity.COMMENT, comment))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPlace() {
        apolloClient.query(PlaceQuery.builder().place(placeId).build())
                .enqueue({
                    swipeRefreshLayout.isRefreshing = false
                    allProducts = it.data()?.place?.products!!
                    updateList()
                }, {
                    swipeRefreshLayout.isRefreshing = false
                })
    }

    private fun updateList() {
        getAdapter().updateProducts(purchaseItems.map {pId ->
            allProducts.find { it._id == pId }!!
        }.groupBy { it._id }, comment )
    }
}
