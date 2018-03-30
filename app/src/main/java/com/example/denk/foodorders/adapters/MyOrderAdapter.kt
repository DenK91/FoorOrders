package com.example.denk.foodorders.adapters

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import com.example.denk.foodorders.PlaceQuery
import com.example.denk.foodorders.R
import com.example.denk.foodorders.adapters.holders.*
import com.example.denk.foodorders.listen
import kotlinx.android.synthetic.main.item_comment.*
import org.jetbrains.anko.layoutInflater

class MyOrderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewType { COMMENT, PRODUCT }

    private var products: List<List<PlaceQuery.Product>>? = null
    private var comment: String? = null

    override fun getItemCount(): Int = (products?.size ?: 0) + if(comment.isNullOrBlank().not()) 1 else 0

    private lateinit var addProductClickListener: (product: PlaceQuery.Product) -> Unit
    private lateinit var removeProductClickListener: (product: PlaceQuery.Product) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            ViewType.PRODUCT.ordinal -> {
                val view = parent.context?.layoutInflater?.inflate(R.layout.item_product_in_sub_order, parent, false)!!
                return ProductHolder(view).listenAddProduct {
                    addProductClickListener.invoke(it)
                }.listenRemoveProduct {
                    removeProductClickListener.invoke(it)
                }
            }
            ViewType.COMMENT.ordinal -> {
                val view = parent.context?.layoutInflater?.inflate(R.layout.item_comment, parent, false)!!
                return CommentHolder(view)
            }

            else -> throw RuntimeException("Not supported view type!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(comment.isNullOrBlank().not() && position == products?.size)
            ViewType.COMMENT.ordinal
        else
            ViewType.PRODUCT.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductHolder ->  holder.bind(products!![position])
            is CommentHolder -> holder.tvComment.text = comment
        }
    }


    fun updateProducts(products: Map<String?, List<PlaceQuery.Product>>, comment: String?) {
        this.products = products.values.toList()
        this.comment = comment;
        notifyDataSetChanged()
    }

    fun listenAddProduct(event: (product: PlaceQuery.Product) -> Unit): MyOrderAdapter {
        addProductClickListener = {
            event.invoke(it)
        }
        return this
    }

    fun listenRemoveProduct(event: (product: PlaceQuery.Product) -> Unit): MyOrderAdapter {
        removeProductClickListener = {
            event.invoke(it)
        }
        return this
    }

}
