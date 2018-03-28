package com.example.denk.foodorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.denk.foodorders.OrdersQuery
import com.example.denk.foodorders.R
import com.example.denk.foodorders.listen
import com.example.denk.foodorders.toDateString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*
import org.jetbrains.anko.layoutInflater

class OrdersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataset = emptyList<OrdersQuery.Order>().toMutableList()

    override fun getItemCount(): Int = dataset.size

    private lateinit var itemClickListener: (order: OrdersQuery.Order) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            OrderHolder(parent.context?.layoutInflater?.inflate(R.layout.item_order, parent, false)!!)
                    .listen { itemClickListener.invoke(dataset[it]) }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OrderHolder)?.bind(dataset[position])
    }

    fun updateOrders(orders: List<OrdersQuery.Order>?) {
        dataset.clear()
        if (orders != null && orders.isNotEmpty()) {
            dataset.addAll(orders)
        }
        notifyDataSetChanged()
    }

    fun itemClickedListen(event: (order: OrdersQuery.Order) -> Unit) {
        itemClickListener = event
    }

    class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(order: OrdersQuery.Order) {
            tvOrderTitle.text = order.place().description()
            tvAuthor.text = "${order.user().first_name()}  ${order.user().last_name()}"
            tvTimestamp.text = order.date()?.toDateString()
        }
    }

}
