package com.example.denk.foodorders

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class FinishOrderActivity : AppCompatActivity() {

    companion object {
        const val ORDER_ID_KEY: String = "order_id_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_order)
    }
}
