package com.example.denk.foodorders

import android.support.v7.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by denk on 23.03.18.
 */
fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition())
    }
    return this
}

fun Long.toDateString(format: String = "dd MMMM HH:mm"): String {
    return SimpleDateFormat(format, Locale.getDefault())
            .format(this.times(1000));
}