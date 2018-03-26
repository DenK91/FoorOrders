package com.example.denk.foodorders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*
import org.jetbrains.anko.layoutInflater
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataset = emptyList<GetListOrdersQuery.Order>().toMutableList()

    override fun getItemCount(): Int = dataset.size

    private lateinit var itemClickListener: (order: GetListOrdersQuery.Order) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
            OrderHolder(parent?.context?.layoutInflater?.inflate(R.layout.item_order, parent, false)!!)
                    .listen { itemClickListener.invoke(dataset[it]) }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? OrderHolder)?.bind(dataset[position])
    }

    fun updateOrders(orders: List<GetListOrdersQuery.Order>?) {
        dataset.clear()
        if (orders != null && orders.isNotEmpty()) {
            dataset.addAll(orders)
        }
        notifyDataSetChanged()
    }

    fun itemClickedListen(event: (order: GetListOrdersQuery.Order) -> Unit) {
        itemClickListener = event
    }

    class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val sdf = SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault())

        fun bind(order: GetListOrdersQuery.Order) {
            tvOrderTitle.text = order.place.decription
            tvAuthor.text = "${order.admin.first_name}  ${order.admin.last_name}"
            tvTimestamp.text = sdf.format(order.date?.times(1000))
        }
    }

}
