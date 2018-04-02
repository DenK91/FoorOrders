package com.example.denk.foodorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.denk.foodorders.OrderQuery
import com.example.denk.foodorders.R
import com.example.denk.foodorders.adapters.holders.*
import com.example.denk.foodorders.listen
import org.jetbrains.anko.layoutInflater

class OrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType { HEADER, COMMENT, SUBORDER }

    private var order: OrderQuery.Order? = null
    private var deliveryCost: Long? = null

    override fun getItemCount(): Int = order?.let {
        return 1 + (it.subOrders()?.size ?: 0) + (it.comments()?.size ?: 0)
    } ?: 0

    private lateinit var subOrderClickListener: (order: OrderQuery.SubOrder) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.HEADER.ordinal -> OrderHolder(parent.context?.layoutInflater
                    ?.inflate(R.layout.item_order, parent, false)!!)
            ViewType.COMMENT.ordinal -> CommentHolder(parent.context?.layoutInflater
                    ?.inflate(R.layout.item_comment, parent, false)!!)
            ViewType.SUBORDER.ordinal -> SubOrderHolder(parent.context?.layoutInflater
                    ?.inflate(R.layout.item_suborder, parent, false)!!)
                    .listen { order?.subOrders()?.get(it -1)
                            .let { it1 ->
                                if (it1 != null) {
                                    subOrderClickListener.invoke(it1)
                                }
                            }
                    }
            else -> throw RuntimeException("View type is not supported!")
        }
    }

    private fun subordersEnd(): Int = (order?.subOrders()?.size ?: 0)

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ViewType.HEADER.ordinal
            in 0..subordersEnd() -> ViewType.SUBORDER.ordinal
            else -> ViewType.COMMENT.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderHolder -> order?.let { holder.bind(it) }
            is SubOrderHolder -> order?.subOrders()?.get(position - 1)?.let { holder.bind(it, deliveryCost!!) }
            is CommentHolder -> order?.comments()?.get(position - 1 - subordersEnd())?.let { holder.bind(it) }
        }
    }

    fun updateOrder(order: OrderQuery.Order) {
        this.order = order
        var sum : Long = 0
        order.subOrders()?.flatMap { it.products()?.toList()!! }?.groupBy { it._id() }
                ?.map { it.value }?.toMutableList()?.forEach { it.forEach { sum +=it.price() } }
        if (sum < order.place().delivery_limit()!! && order.subOrders()?.isNotEmpty()!!) {
            this.deliveryCost = order.place().delivery_cost()!! / order.subOrders()!!.size
        } else {
            this.deliveryCost = 0
        }
        notifyDataSetChanged()
    }

    fun subOrderClickedListen(event: (order: OrderQuery.SubOrder) -> Unit) {
        subOrderClickListener = event
    }

}
