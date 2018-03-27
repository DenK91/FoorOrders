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
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType{ HEADER, COMMENT, SUBORDER}

    private var order : GetOrderQuery.OrderById? = null

    override fun getItemCount(): Int = order?.let { return 1 + (it.suborders?.size ?: 0) + (it.comments?.size ?: 0) } ?: 0

    private lateinit var itemClickListener: (order: GetOrderQuery.OrderById) -> Unit

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

    fun subordersEnd(): Int = (order?.suborders?.size ?: 0)


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
            is SubOrderHolder -> order?.suborders?.get(position - 1)?.let { holder.bind(it) }
            is CommentHolder -> order?.comments?.get(position - 1 - subordersEnd())?.let { holder.bind(it) }
        }
    }

    fun updateOrder(order: GetOrderQuery.OrderById) {
        this.order = order
        notifyDataSetChanged()
    }

    fun itemClickedListen(event: (order: GetOrderQuery.OrderById) -> Unit) {
        itemClickListener = event
    }

    class OrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(order: GetOrderQuery.OrderById) {
            tvOrderTitle.text = order.place.decription
            tvAuthor.text = "${order.admin.first_name}  ${order.admin.last_name}"
            tvTimestamp.text = order.date?.toDateString()
        }
    }

    class SubOrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(suborder: GetOrderQuery.Suborder) {
            tvSuborderAuthor.text = "${suborder.author.first_name}  ${suborder.author.last_name}"
            tvSuborderSum.text = "${NumberFormat.getCurrencyInstance().format(suborder.products?.sumBy { it.price.toInt() })} "
        }
    }

    class CommentHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(comment: GetOrderQuery.Comment) {
            tvCommentAuthor.text = "${comment.author?.first_name}  ${comment.author?.last_name}"
            tvComment.text = comment.text
        }
    }

}
