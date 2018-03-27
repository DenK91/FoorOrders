package com.example.denk.foodorders

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import kotlinx.android.synthetic.main.activity_create_suborder.*

class CreateSuborderActivity : AppCompatActivity(){

    companion object {
        val PLACE_ID = "place_id"
    }

    lateinit var placeId : String
    val uiHandler = Handler(Looper.getMainLooper())

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_suborder, menu)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_suborder)
        placeId = intent.getStringExtra(PLACE_ID)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        rvMenu.adapter = ProductsAdapter()
        rvMenu.layoutManager = LinearLayoutManager(this)
        getOrder()
    }

    private fun getOrder() {
        apolloClient.query(PlaceQuery.builder().place(placeId).build())
                .enqueue({setMenu(it.data()?.place?.products!!)})
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun setMenu(menu: List<PlaceQuery.Product>) {
        (rvMenu.adapter as? ProductsAdapter)?.data = menu
    }
}
