package com.example.denk.foodorders.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.denk.foodorders.OrderQuery
import com.example.denk.foodorders.toDateString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*

class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(order: OrderQuery.Order) {
        tvOrderTitle.text = order.place().description()
        tvAuthor.text = "${order.user().first_name()}  ${order.user().last_name()}"
        tvTimestamp.text = order.date()?.toDateString()
    }
}