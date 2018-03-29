package com.example.denk.foodorders.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.denk.foodorders.*
import kotlinx.android.synthetic.main.item_product_mini.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MiniProductView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.item_product_mini, this, true)
        orientation = VERTICAL
    }

    lateinit var addProductClickListener: (product: PlaceQuery.Product) -> Unit
    lateinit var removeProductClickListener: (product: PlaceQuery.Product) -> Unit

    fun setProducts(products: List<OrderQuery.Product>) {
        controls.visibility = View.GONE
        ivProductImage.load(products.first().photo())
        tvProductName.text = products[0].name()
        tvProductPrice.text = products[0].price().toInt().toCurrencyString() + " x ${products.size}"
        tvProductDescription.text = products[0].description()
    }

    fun setProducts2(products: List<PlaceQuery.Product>) {
        controls.visibility = View.VISIBLE
        ivProductImage.load(products.first().photo())
        tvProductName.text = products[0].name()
        tvProductPrice.text = products[0].price().toInt().toCurrencyString()
        tvProductDescription.text = products[0].description()
        tvProductsCount.text = products.size.toString()
        btnAddProduct.onClick {
            addProductClickListener.invoke(products.first())
        }
        btnRemoveProduct.onClick {
            removeProductClickListener.invoke(products.first())
        }
    }
}