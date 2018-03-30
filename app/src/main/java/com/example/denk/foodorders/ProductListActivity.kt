package com.example.denk.foodorders

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.apollographql.apollo.api.Input
import com.example.denk.foodorders.adapters.ProductsAdapter
import com.example.denk.foodorders.views.CounterDrawable
import kotlinx.android.synthetic.main.activity_create_suborder.*
import org.jetbrains.anko.*

class ProductListActivity : AppCompatActivity() {

    companion object {
        const val PLACE_ID = "place_id"
        const val ORDER_ID = "order_id"
        const val SUB_ORDER_ID = "sub_order_id"
        const val PRODUCTS_ID = "products_id"
        const val COMMENT = "comment"
    }

    private lateinit var placeId: String
    private lateinit var orderId: String
    private var subOrderId: String? = null
    private lateinit var allProducts: List<PlaceQuery.Product>
    private var purchaseItems = arrayListOf<String>()
    private var comment: String? = null
    private var cart: MenuItem? = null

    private val purchaseAdapter = object : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemId = getItem(position)
            val itemName = allProducts.first { it._id == itemId }.name
            val v = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            v.findViewById<TextView>(android.R.id.text1).text = itemName
            return v
        }

        override fun getItem(position: Int): Any = purchaseItems[position]
        override fun getItemId(position: Int): Long = purchaseItems.hashCode().toLong()
        override fun getCount(): Int = purchaseItems.size

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_suborder, menu)
        cart = menu?.findItem(R.id.shopping_cart)
        if (subOrderId != null) {
            cart?.isVisible = true
            cart?.icon?.setCount(purchaseItems.size)
        } else {
            cart?.isVisible = false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_suborder)
        placeId = intent.getStringExtra(PLACE_ID)
        orderId = intent.getStringExtra(ORDER_ID)
        val subOrder = intent.getStringExtra(SUB_ORDER_ID)
        if (subOrder != null) {
            subOrderId = subOrder
        }
        val products = intent.getStringArrayListExtra(PRODUCTS_ID)
        if (products != null && !products.isEmpty()) {
            purchaseItems = products
        }
        comment = intent.getStringExtra(COMMENT)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        rvMenu.adapter = ProductsAdapter()
        rvMenu.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {
            if (purchaseItems.isEmpty()) {
                apolloClient.mutate(SubOrderMutation(prefs.userId, orderId, Input.fromNullable(listOf(it._id))))
                        .enqueue({
                            subOrderId = it.data()?.subOrder?._id
                            cart?.isVisible = true
                            invalidateOptionsMenu()
                        })
            } else {
                apolloClient.mutate(EditSubOrderMutation(subOrderId!!, Input.fromNullable(null), Input.fromNullable(purchaseItems)))
                        .enqueue({})
            }
            purchaseItems.add(it._id)
            //do some animations
            val index = getAdapter().data.indexOf(it)
            val itemView = rvMenu.layoutManager.findViewByPosition(index)
            itemView.animOut { invalidateOptionsMenu() }
            getAdapter().remove(it)
        }
        getOrder()
    }

    private fun getOrder() {
        apolloClient.query(PlaceQuery.builder().place(placeId).build())
                .enqueue({
                    allProducts = it.data()?.place?.products!!
                    setMenu(it.data()?.place?.products?.filter { !purchaseItems.contains(it._id) }!!.toMutableList())
                })
    }

    private fun getAdapter() = rvMenu.adapter as ProductsAdapter

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.shopping_cart -> {
                startActivity(intentFor<SubOrderActivity>()
                        .putExtra(SubOrderActivity.PLACE_ID, placeId)
                        .putExtra(SubOrderActivity.ORDER_ID, orderId)
                        .putExtra(SubOrderActivity.SUB_ORDER_ID, subOrderId)
                        .putExtra(SubOrderActivity.PRODUCTS_ID, purchaseItems)
                        .putExtra(SubOrderActivity.COMMENT, comment))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setMenu(menu: MutableList<PlaceQuery.Product>) {
        getAdapter().data = menu
    }

    private fun Drawable.setCount(count: Int) {
        val bg = this as? LayerDrawable
        bg?.apply {
            val drawable = (findDrawableByLayerId(R.id.dCounter) as? CounterDrawable)
                    ?: CounterDrawable(applicationContext)
            drawable.count = count.toString()
            setDrawableByLayerId(R.id.dCounter, drawable)
        }
    }
}
