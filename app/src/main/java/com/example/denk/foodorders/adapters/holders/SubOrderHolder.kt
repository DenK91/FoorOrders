package com.example.denk.foodorders.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.denk.foodorders.OrderQuery
import com.example.denk.foodorders.views.MiniProductView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_suborder.*
import java.text.NumberFormat

class SubOrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(suborder: OrderQuery.SubOrder) {
        tvSuborderAuthor.text = "${suborder.user().first_name()}  ${suborder.user().last_name()}"
        tvSuborderSum.text = "${NumberFormat.getCurrencyInstance().format(suborder.products()?.sumBy { it.price().toInt() })} "
        productsContainer.removeAllViews()

        suborder.products()?.groupBy { it._id() }?.values?.forEach {
            val miniProductView = MiniProductView(itemView.context)
            productsContainer.addView(miniProductView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            miniProductView.setProducts(it)
        }

    }
}