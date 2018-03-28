package com.example.denk.foodorders

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product.*

class ProductsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private lateinit var itemClickListener: (product: PlaceQuery.Product) -> Unit
    var data: List<PlaceQuery.Product> = emptyList()
    set(value){
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.item_product, parent, false)
        return ProductHolder(v).listen {
            itemClickListener.invoke(data[it])
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as? ProductHolder)?.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ProductHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(menu: PlaceQuery.Product){
            tvProductName.text = menu.name
            ivProductImage.load(menu.photo)
            tvProductPrice.text = menu.price.toInt().toCurrencyString()
            tvProductDescription.text = menu.description
        }
    }

    fun itemClickedListen(event: (product: PlaceQuery.Product) -> Unit) {
        itemClickListener = event
    }
}
