package com.example.denk.foodorders.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.denk.foodorders.OrderQuery
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_suborder.*

class ProductHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(product: OrderQuery.Product) {
        tvSuborderAuthor.text = product.name()
        tvSuborderSum.text = "${product.price()}"
    }
}