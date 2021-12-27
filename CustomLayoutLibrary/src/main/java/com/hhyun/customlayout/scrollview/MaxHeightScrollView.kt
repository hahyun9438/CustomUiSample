package com.hhyun.customlayout.scrollview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ScrollView
import com.hhyun.customlayout.R

class MaxHeightScrollView: ScrollView {

    private var maxHeight = 0

    constructor(context: Context) : super(context) { init(context, null) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(context, attrs) }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val styledAttrs: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightScrollView)
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight, 200) //200 is a default value
            styledAttrs.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mHeightMeasureSpec = heightMeasureSpec
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }


    fun setMaxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
        requestLayout()
    }

}