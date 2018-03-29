package com.example.denk.foodorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.denk.foodorders.PlaceQuery
import com.example.denk.foodorders.R
import com.example.denk.foodorders.adapters.holders.*
import org.jetbrains.anko.layoutInflater

class MyOrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var products: List<List<PlaceQuery.Product>>? = null

    override fun getItemCount(): Int = products?.size ?: 0

    private lateinit var addProductClickListener: (product: PlaceQuery.Product) -> Unit
    private lateinit var removeProductClickListener: (product: PlaceQuery.Product) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = parent.context?.layoutInflater?.inflate(R.layout.item_product_in_sub_order, parent, false)!!
        return ProductHolder(view).listenAddProduct {
            addProductClickListener.invoke(it)
        }.listenRemoveProduct {
            removeProductClickListener.invoke(it)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductHolder ->  holder.bind(products!![position])
        }
    }

    fun updateProducts(products: Map<String?, List<PlaceQuery.Product>>) {
        this.products = products.values.toList()
        notifyDataSetChanged()
    }

    fun listenAddProduct(event: (product: PlaceQuery.Product) -> Unit): MyOrderAdapter {
        addProductClickListener = {
            event.invoke(it)
        }
        return this
    }

    fun listenRemoveProduct(event: (product: PlaceQuery.Product) -> Unit): MyOrderAdapter {
        removeProductClickListener = {
            event.invoke(it)
        }
        return this
    }

}
