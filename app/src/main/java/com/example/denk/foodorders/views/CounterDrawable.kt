package com.example.denk.foodorders.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.example.denk.foodorders.R

/**
 * Created by oleg on 28/03/2018.
 */
class CounterDrawable(val context: Context): Drawable() {

    private val mBadgePaint: Paint
    private val mTextPaint: Paint
    private val mTxtRect = Rect()

    var count = ""
        set(value) {
            field = value
            mWillDraw = value != "0"
            invalidateSelf()
        }
    private var mWillDraw: Boolean = false

    init {
    val mTextSize = context.getResources().getDimension(R.dimen.badge_count_textsize);

    mBadgePaint = Paint();
    mBadgePaint.setColor(Color.RED);
    mBadgePaint.setAntiAlias(true);
    mBadgePaint.setStyle(Paint.Style.FILL);

    mTextPaint = Paint();
    mTextPaint.setColor(Color.WHITE);
    mTextPaint.setTypeface(Typeface.DEFAULT);
    mTextPaint.setTextSize(mTextSize);
    mTextPaint.setAntiAlias(true);
    mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

override fun draw(canvas: Canvas) {

    if (!mWillDraw) {
        return;
    }
    val width = bounds.right - bounds.left;
    val height = bounds.bottom - bounds.top;

    val radius = ((Math.max(width, height) / 2)) / 2;
    val centerX = ((width - radius - 1) +5).toFloat();
    val centerY = (radius -5).toFloat();
    if(count.length <= 2){
        // Draw badge circle.
        canvas.drawCircle(centerX, centerY, (radius+5.5f), mBadgePaint);
    }
    else{
        canvas.drawCircle(centerX, centerY, (radius+6.5f), mBadgePaint);
    }
    // Draw badge count text inside the circle.
    mTextPaint.getTextBounds(count, 0, count.length, mTxtRect);
    val textHeight = mTxtRect.bottom - mTxtRect.top;
    val textY = centerY + (textHeight / 2f);
    canvas.drawText(if(count.length > 2)"99+" else count, centerX, textY, mTextPaint);
}

    override fun setAlpha(alpha: Int) {}

    override fun getOpacity(): Int = PixelFormat.UNKNOWN

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    var counter : Int = 0

}