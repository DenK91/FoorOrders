package com.example.denk.foodorders.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.denk.foodorders.OrderQuery
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_comment.*

class CommentHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(comment: OrderQuery.Comment) {
        tvCommentAuthor.text = "${comment.user()?.first_name()}  ${comment.user()?.last_name()}"
        tvComment.text = comment.text()
    }
}