package com.hhyun.customlayout.scrollview

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.ScrollView

class LockableScrollView: ScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val TAG = "LockableScrollView"
    private var mScrollable = true

    fun setScrollingEnabled(enabled: Boolean) {
        mScrollable = enabled
    }

    fun isScrollable(): Boolean {
        return mScrollable
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        Log.d(TAG, "onTouchEvent in = $mScrollable")
        return when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mScrollable) super.onTouchEvent(ev)
                else mScrollable
            }
            else -> super.onTouchEvent(ev)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        Log.d(TAG, "onInterceptTouchEvent in = $mScrollable")
        return if (!mScrollable) false else super.onInterceptTouchEvent(ev)
    }
}