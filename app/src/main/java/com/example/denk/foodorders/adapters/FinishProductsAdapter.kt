package com.example.denk.foodorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.denk.foodorders.*
import com.example.denk.foodorders.adapters.holders.ProductHolder

class FinishProductsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: MutableList<List<OrderQuery.Product>> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ProductHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product_in_sub_order, parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ProductHolder)?.bindWithoutControls(data[position])
    }
}
