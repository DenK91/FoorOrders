package com.example.denk.foodorders

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.apollographql.apollo.api.Input
import kotlinx.android.synthetic.main.activity_create_suborder.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class CreateSuborderActivity : AppCompatActivity(){

    companion object {
        val PLACE_ID = "place_id"
        val ORDER_ID = "order_id"
    }

    private lateinit var placeId : String
    private lateinit var orderId: String
    val uiHandler = Handler(Looper.getMainLooper())

    val purchaseItems = arrayListOf<String>()
    val purchaseAdapter = object : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val itemId = getItem(position)
            var itemName = getAdapter().data.first { it._id.equals(itemId) }.name
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
        return true
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_suborder)
        placeId = intent.getStringExtra(PLACE_ID)
        orderId = intent.getStringExtra(ORDER_ID)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        rvMenu.adapter = ProductsAdapter()
        rvMenu.layoutManager = LinearLayoutManager(this)
        getAdapter().itemClickedListen {
            purchaseItems.add(it._id)
        }
        getOrder()
    }

    private fun getOrder() {
        apolloClient.query(PlaceQuery.builder().place(placeId).build())
                .enqueue({setMenu(it.data()?.place?.products!!)})
    }
    fun getAdapter() = rvMenu.adapter as ProductsAdapter

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.shopping_cart -> {
                showShoppingDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showShoppingDialog() {
        alert {
            customView {
                verticalLayout {
                    listView {
                        adapter = purchaseAdapter
                    }
                    button {
                        text = "Сделать заказ"
                        onClick {
                            apolloClient
                                    .mutate(SubOrderMutation(prefs.userId, orderId, Input.fromNullable(purchaseItems)))
                                    .enqueue({ finish() })
                        }
                    }
                }
            }

        }.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun setMenu(menu: List<PlaceQuery.Product>) {
        getAdapter()?.data = menu
    }
}
