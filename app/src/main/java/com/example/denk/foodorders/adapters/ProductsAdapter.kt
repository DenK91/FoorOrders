package com.example.denk.foodorders.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.denk.foodorders.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*

class ProductsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClickListener: (product: PlaceQuery.Product) -> Unit
    var data: MutableList<PlaceQuery.Product> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductHolder(v).listen {
            itemClickListener.invoke(data[it])
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ProductHolder)?.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ProductHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(menu: PlaceQuery.Product) {
            tvProductName.text = menu.name()
            ivProductImage.load(menu.photo())
            tvProductPrice.text = menu.price().toInt().toCurrencyString()
            tvProductDescription.text = menu.description()
        }
    }

    fun itemClickedListen(event: (product: PlaceQuery.Product) -> Unit) {
        itemClickListener = event
    }

    fun remove(product: PlaceQuery.Product) {
        val pos = data.indexOf(product)
        if (pos != -1) {
            data.remove(product)
            notifyItemRemoved(pos)
        }
    }
}
