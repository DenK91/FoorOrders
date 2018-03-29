package com.example.denk.foodorders.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.denk.foodorders.PlaceQuery
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_product_in_sub_order.*

class ProductHolder(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(products: List<PlaceQuery.Product>) : ProductHolder {
        miniProductView.setProducts2(products)
        return this
    }

    fun listenAddProduct(event: (product: PlaceQuery.Product) -> Unit): ProductHolder {
        miniProductView.addProductClickListener = {
            event.invoke(it)
        }
        return this
    }

    fun listenRemoveProduct(event: (product: PlaceQuery.Product) -> Unit): ProductHolder {
        miniProductView.removeProductClickListener = {
            event.invoke(it)
        }
        return this
    }

}