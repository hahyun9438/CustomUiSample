package com.hhyun.customlayout.pager

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager

class WrapContentViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        for (i in 0 until childCount){
            val child = getChildAt (i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = Math.max(child.measuredHeight, measureViewHeight(child, widthMeasureSpec))
            if (h > height) {
                height = h
            }
        }

        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    private fun measureViewHeight(view: View, widthMeasuredSpec: Int): Int {
        view.measure(
            getChildMeasureSpec(
                widthMeasuredSpec,
                paddingLeft + paddingRight,
                view.layoutParams.width),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        return view.measuredHeight
    }
}