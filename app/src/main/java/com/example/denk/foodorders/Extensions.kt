package com.example.denk.foodorders

import android.support.v7.widget.RecyclerView

/**
 * Created by denk on 23.03.18.
 */
fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition())
    }
    return this
}