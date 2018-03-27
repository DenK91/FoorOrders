package com.example.denk.foodorders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_comment.*
import kotlinx.android.synthetic.main.item_order.*
import kotlinx.android.synthetic.main.item_suborder.*
import org.jetbrains.anko.layoutInflater
import java.text.NumberFormat

class OrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType{ HEADER, COMMENT, SUBORDER}

    private var order : OrderQuery.Order? = null

    override fun getItemCount(): Int = order?.let { return 1 + (it.subOrders?.size ?: 0) + (it.comments?.size ?: 0) } ?: 0

    private lateinit var itemClickListener: (order: OrderQuery.Order) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder{
        when(viewType){
            ViewType.HEADER.ordinal -> {
                val view = parent?.context?.layoutInflater?.inflate(R.layout.item_order, parent, false)!!
                return OrderHolder(view).listen { order?.let { it1 -> itemClickListener.invoke(it1) } }
            }
            ViewType.SUBORDER.ordinal -> {
                val view = parent?.context?.layoutInflater?.inflate(R.layout.item_suborder, parent, false)!!
                return SubOrderHolder(view).listen { order?.let { it1 -> itemClickListener.invoke(it1) } }
            }
            ViewType.COMMENT.ordinal -> {
                val view = parent?.context?.layoutInflater?.inflate(R.layout.item_comment, parent, false)!!
                return CommentHolder(view).listen { order?.let { it1 -> itemClickListener.invoke(it1) } }
            }
            else -> throw RuntimeException("View type is not supported!");
        }
    }

    fun subordersEnd(): Int = (order?.subOrders?.size ?: 0)


    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> ViewType.HEADER.ordinal
            in 0..subordersEnd() -> ViewType.SUBORDER.ordinal
            else -> ViewType.COMMENT.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is OrderHolder -> order?.let{holder.bind(it)}
            is SubOrderHolder -> order?.subOrders?.get(position - 1)?.let { holder.bind(it) }
            is CommentHolder -> order?.comments?.get(position - 1 - subordersEnd())?.let { holder.bind(it) }
        }
    }

    fun updateOrder(order: OrderQuery.Order) {
        this.order = order
        notifyDataSetChanged()
    }

    fun itemClickedListen(event: (order: OrderQuery.Order) -> Unit) {
        itemClickListener = event
    }

    class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(order: OrderQuery.Order) {
            tvOrderTitle.text = order.place.description
            tvAuthor.text = "${order.user.first_name}  ${order.user.last_name}"
            tvTimestamp.text = order.date?.toDateString()
        }
    }

    class SubOrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(suborder: OrderQuery.SubOrder) {
            tvSuborderAuthor.text = "${suborder.user.first_name}  ${suborder.user.last_name}"
            tvSuborderSum.text = "${NumberFormat.getCurrencyInstance().format(suborder.products?.sumBy { it.price.toInt() })} "
        }
    }

    class CommentHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(comment: OrderQuery.Comment) {
            tvCommentAuthor.text = "${comment.user?.first_name}  ${comment.user?.last_name}"
            tvComment.text = comment.text
        }
    }

}
