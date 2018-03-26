package com.example.denk.foodorders

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import org.jetbrains.anko.layoutInflater
import android.widget.TextView

class PlaceAdapter(val ctx: Context, private val dataset: MutableList<PlacesQuery.Place>) : BaseAdapter() {

    var lInflater: LayoutInflater = ctx.layoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = lInflater.inflate(R.layout.item_place, parent, false)
        }

        val p = dataset[position]

        val tvPlaceName = (view!!.findViewById(R.id.tvPlaceName) as TextView)
        val tvPlaceDescription = (view.findViewById(R.id.tvPlaceDescription) as TextView)
        val tvPhone = (view.findViewById(R.id.tvPhone) as TextView)
        val tvSite = (view.findViewById(R.id.tvSite) as TextView)
        if (TextUtils.isEmpty(p.name)) {
            tvPlaceName.visibility = View.GONE
        } else {
            tvPlaceName.visibility = View.VISIBLE
            tvPlaceName.text = p.name
        }
        if (TextUtils.isEmpty(p.decription)) {
            tvPlaceDescription.visibility = View.GONE
        } else {
            tvPlaceDescription.visibility = View.VISIBLE
            tvPlaceDescription.text = p.decription
        }
        if (TextUtils.isEmpty(p.phone)) {
            tvPhone.visibility = View.GONE
        } else {
            tvPhone.visibility = View.VISIBLE
            tvPhone.text = p.phone
        }
        if (TextUtils.isEmpty(p.site)) {
            tvSite.visibility = View.GONE
        } else {
            tvSite.visibility = View.VISIBLE
            tvSite.text = p.site
        }
        return view
    }

    override fun getItem(position: Int): PlacesQuery.Place = dataset[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = dataset.size

}