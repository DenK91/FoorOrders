package com.example.denk.foodorders.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.denk.foodorders.OrderQuery
import com.example.denk.foodorders.R
import com.example.denk.foodorders.load
import com.example.denk.foodorders.toCurrencyString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_suborder.*
import org.jetbrains.anko.find
import org.jetbrains.anko.layoutInflater
import java.text.NumberFormat

class SubOrderHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(suborder: OrderQuery.SubOrder) {
        tvSuborderAuthor.text = "${suborder.user().first_name()}  ${suborder.user().last_name()}"
        tvSuborderSum.text = "${NumberFormat.getCurrencyInstance().format(suborder.products()?.sumBy { it.price().toInt() })} "
        productsContainer.removeAllViews()

        suborder.products()?.groupBy { it._id() }?.values?.forEach {
            val v = itemView.context.layoutInflater.inflate(R.layout.item_product_mini, productsContainer)
            v.find<ImageView>(R.id.ivProductImage).load(it[0].photo())
            v.find<TextView>(R.id.tvProductName).text = it[0].name()
            v.find<TextView>(R.id.tvProductPrice).text = it[0].price().toInt().toCurrencyString()
            v.find<TextView>(R.id.tvProductDescription).text = it[0].description()
        }

    }
}