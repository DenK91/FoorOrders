package com.example.denk.foodorders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_order.*
import org.jetbrains.anko.layoutInflater
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var order : GetOrderQuery.OrderById? = null

    override fun getItemCount(): Int = if (order != null) 1 else 0

    private lateinit var itemClickListener: (order: GetOrderQuery.OrderById) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder =
            OrderHolder(parent?.context?.layoutInflater?.inflate(R.layout.item_order, parent, false)!!)
                    .listen { order?.let { it1 -> itemClickListener.invoke(it1) } }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        order?.let { (holder as? OrderHolder)?.bind(it) }
    }

    fun updateOrder(order: GetOrderQuery.OrderById) {
        this.order = order
        notifyDataSetChanged()
    }

    fun itemClickedListen(event: (order: GetOrderQuery.OrderById) -> Unit) {
        itemClickListener = event
    }

    class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val sdf = SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault())

        fun bind(order: GetOrderQuery.OrderById) {
            tvOrderTitle.text = order.place.decription
            tvAuthor.text = "${order.admin.first_name}  ${order.admin.last_name}"
            tvTimestamp.text = sdf.format(order.date?.times(1000))
        }
    }

}
