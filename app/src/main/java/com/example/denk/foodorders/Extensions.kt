package com.example.denk.foodorders

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.squareup.picasso.Picasso
import org.jetbrains.anko.image
import java.text.NumberFormat
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

fun Int.toCurrencyString(): String{
    return NumberFormat.getCurrencyInstance().format(this)
}

fun Long.toDateString(format: String = "dd MMMM HH:mm"): String {
    return SimpleDateFormat(format, Locale.getDefault())
            .format(this.times(1000));
}

fun <T> ApolloCall<T>.enqueue(
        onSuccess: (response: Response<T>) -> Unit,
        onFailure: (ex: ApolloException) -> Unit = { it.printStackTrace() }) {

    this.enqueue(ApolloCallback<T>(object : ApolloCall.Callback<T>() {
        override fun onFailure(e: ApolloException) = onFailure(e)

        override fun onResponse(response: Response<T>) = onSuccess(response)

    }, Handler(Looper.getMainLooper())))

}

fun ImageView.load(url: String?){
    if(!TextUtils.isEmpty(url)) {
        Picasso.get().load(url)
                .resize(1080, 1080)
                .centerCrop()
                .into(this)
    }
}

fun View.animOut(scale: Float = 0.1f, duration: Long = 300, callback: ()->Unit = {}){
    val rootLayout = rootView as ViewGroup
    isDrawingCacheEnabled = true
    buildDrawingCache()
    val bitmap = Bitmap.createBitmap(getDrawingCache())
    destroyDrawingCache()

    val offset = Rect()
    val size = Rect()
    getDrawingRect(size)
    getGlobalVisibleRect(offset)
    val imageView = ImageView(context)
    imageView.image = BitmapDrawable(resources, bitmap)
    rootLayout.addView(imageView, FrameLayout.LayoutParams(size.width(), size.height())
            .apply { leftMargin = offset.left; topMargin = offset.top })

    imageView.animate()
            .scaleX(scale)
            .scaleY(scale)
            .translationX((size.width()*(1-scale).times(0.5)).toFloat())
            .translationY((offset.top + size.height().times(0.5 + scale)).toFloat().unaryMinus())
            .setDuration(duration)
            .alpha(0.1f)
            .withEndAction {
                rootLayout.removeView(imageView)
                bitmap?.recycle()
                callback.invoke()
            }
}