package com.example.denk.foodorders.adapters.holders

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.denk.foodorders.OrderQuery
import com.example.denk.foodorders.OrdersQuery
import com.example.denk.foodorders.toDateString
import com.example.denk.foodorders.type.State
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*
import org.jetbrains.anko.textColor

class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(order: OrderQuery.Order) {
        bind(order.place().description(),
                order.user().first_name(),
                order.user().last_name(),
                order.date()?.toDateString(),
                order.state())
    }

    fun bind(order: OrdersQuery.Order) {
        bind(order.place().description(),
                order.user().first_name(),
                order.user().last_name(),
                order.date()?.toDateString(),
                order.state())
    }

    private fun bind(desc: String?, firstName: String?, lastName: String?, timeStamp: String?, state: State) {
        tvOrderTitle.text = desc
        tvAuthor.text = "${firstName}  ${lastName}"
        tvTimestamp.text = timeStamp
        tvStatus.visibility = View.VISIBLE
        when (state) {
            State.ACTIVE -> {
                tvStatus.text = "Выбор продуктов"; tvStatus.textColor = Color.GREEN
            }
            State.PURCHASED -> {
                tvStatus.text = "Доставляется"; tvStatus.textColor = Color.RED
            }
            else -> {
                tvStatus.visibility = View.GONE
            }
        }
    }
}