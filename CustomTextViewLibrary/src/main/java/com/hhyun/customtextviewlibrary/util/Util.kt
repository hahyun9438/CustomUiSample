package com.hhyun.customtextviewlibrary.util

import android.content.Context
import android.graphics.Paint
import android.util.DisplayMetrics
import android.util.TypedValue

object Util {

    fun dpToPx(context: Context, dp: Float): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
    }

    fun dpToPx(context: Context, dp: Int): Int {
        return dpToPx(context, dp.toFloat())
    }

    fun pxToDp(context: Context, px: Float): Int {
        val metrics = context.resources.displayMetrics
        return (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    fun pxToDp(context: Context, px: Int): Int {
        return pxToDp(context, px.toFloat())
    }

    fun getDeviceWidth(context: Context?): Int {
        val dm = context?.resources?.displayMetrics
        return dm?.widthPixels ?: 0
    }

    fun getDeviceHeight(context: Context?): Int {
        val dm = context?.resources?.displayMetrics
        return dm?.heightPixels ?: 0
    }



    fun getTextWidth(context: Context, text: CharSequence?, textSizeOfDp: Int): Float {
        if(text.isNullOrEmpty()) return 0f
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = dpToPx(context, textSizeOfDp).toFloat()
        }

        val characterWidths = FloatArray(text.length)
        paint.getTextWidths(text.toString(), characterWidths)

        return characterWidths.sum()
    }


}