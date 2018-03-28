package com.example.denk.foodorders.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.denk.foodorders.OrderQuery
import com.example.denk.foodorders.R
import com.example.denk.foodorders.load
import com.example.denk.foodorders.toCurrencyString
import kotlinx.android.synthetic.main.item_product_mini.view.*

class MiniProductView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.item_product_mini, this, true)
        orientation = VERTICAL
    }

    fun setProducts(products: List<OrderQuery.Product>) {
        ivProductImage.load(products.first().photo())
        tvProductName.text = products[0].name()
        tvProductPrice.text = products[0].price().toInt().toCurrencyString() + " x ${products.size}"
        tvProductDescription.text = products[0].description()
    }
}