package com.hhyun.customlayout.pager

import android.content.Context
import android.view.animation.Interpolator
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.Math.abs
import java.lang.ref.WeakReference

class AutoLoopViewPager: ViewPager {

    companion object {

        const val DEFAULT_INTERVAL = 1500

        const val LEFT = 0
        const val RIGHT = 1

        /** do nothing when sliding at the last or first item **/
        const val SLIDE_BORDER_MODE_NONE = 0

        /** deliver event to parent when sliding at the last or first item **/
        const val SLIDE_BORDER_MODE_TO_PARENT = 1

        const val SCROLL_WHAT = 0
    }


    /** auto scroll time in milliseconds, default is [.DEFAULT_INTERVAL] **/
    var interval = DEFAULT_INTERVAL.toLong()

    /** auto scroll direction, default is [.RIGHT] **/
    var direction = RIGHT

    /**
     * whether automatic cycle when auto scroll reaching the last or first item,
     * default is true
     */
    var isCycle = true
        set(value) {
            field = value
            mAdapter?.let {
                it.setCycle(value)
                if (field && !value) setCurrentItem(0, false)
            }
        }

    /** whether stop auto scroll when touching, default is true **/
    var stopScrollWhenTouch = true

    /**
     * how to process when sliding at the last or first item, default is
     * [.SLIDE_BORDER_MODE_NONE]
     */
    var slideBorderMode = SLIDE_BORDER_MODE_NONE


    /**
     * scroll factor for auto scroll animation, default is 1.0
     * * set the factor by which the duration of sliding animation will change
     * while auto scrolling
     * **/
    var autoScrollFactor = 1.0
    var swipeScrollFactor = 1.0

    var mFixedHeight: Int = 0
    var scroller: CustomDurationScroller? = null
    var isAutoScroll = false


    private var handler = MyHandler(this)
    private var isStopByTouch = false
    private var touchX = 0f
    private var downX = 0f



    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }


    private fun init() {
        setViewPagerScroller()
        super.addOnPageChangeListener(onPageChangeListener)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var mHeightMeasureSpec = heightMeasureSpec

        if (mFixedHeight != 0) {
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mFixedHeight, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)

        } else {

            MeasureSpec.getMode(heightMeasureSpec)
                .takeIf { it == MeasureSpec.UNSPECIFIED || it == MeasureSpec.AT_MOST }
                ?.apply {

                    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

                    var height = 0
                    for (i in 0 until childCount) {
                        val child = getChildAt(i)
                        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                        val h = child.measuredHeight
                        if (h > height) height = h
                    }

                    mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                }

            super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
        }
    }




    /**
     * start auto scroll
     * first scroll delay time is [.getInterval]
     * @param mills first scroll delay time
     */
    fun startAutoScroll(mills: Long = (interval + (scroller?.duration ?: 0) / autoScrollFactor * swipeScrollFactor).toLong()) {
        isAutoScroll = true
        sendScrollMessage(mills)
    }

    /**
     * stop auto scroll
     */
    fun stopAutoScroll() {
        isAutoScroll = false
        handler.removeMessages(SCROLL_WHAT)
    }




    private fun sendScrollMessage(mills: Long) {
        /** remove messages before, keeps one message is running at most  */
        handler.removeMessages(SCROLL_WHAT)
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, mills)
    }


    /** set ViewPager scroller to change animation duration when sliding*/
    private fun setViewPagerScroller() {

        try {

            androidx.viewpager.widget.ViewPager::class.java.getDeclaredField("sInterpolator").apply {
                this.isAccessible = true
                scroller = CustomDurationScroller(context, this.get(null) as Interpolator)
            }

            androidx.viewpager.widget.ViewPager::class.java.getDeclaredField("mScroller").apply {
                this.isAccessible = true
                set(this@AutoLoopViewPager, scroller)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** scroll only once */
    fun scrollOnce() {

        adapter?.takeIf { it.count > 1 }?.let {

            var mCurrentItem = currentItem

            /*the real position is Handled in LoopPagerAdapterWrapper */
            currentItem = if (direction == LEFT) --mCurrentItem else ++mCurrentItem
        }

    }


    /**
     * if stopScrollWhenTouch is true
     *  * if event is down, stop auto scroll.
     *  * if event is up, start auto scroll again.
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {

            MotionEvent.ACTION_DOWN -> {

                if (stopScrollWhenTouch && isAutoScroll) {
                    isStopByTouch = true
                    stopAutoScroll()
                }

                downX = ev.x
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {

                touchX = ev.x

                // 일정 범위 이하이면 부모뷰에 터치 이벤트 전달
                parent.requestDisallowInterceptTouchEvent(abs(downX - touchX) >= 15)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                if (stopScrollWhenTouch && isStopByTouch) startAutoScroll()
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }


    private class MyHandler(autoLoopViewPager: AutoLoopViewPager) : Handler() {

        private val mAutoLoopViewPager: WeakReference<AutoLoopViewPager> = WeakReference(autoLoopViewPager)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                SCROLL_WHAT -> {
                    this.mAutoLoopViewPager.get()?.let {
                        it.scroller?.setScrollDurationFactor(it.autoScrollFactor)
                        it.scrollOnce()
                        it.scroller?.setScrollDurationFactor(it.swipeScrollFactor)
                        it.sendScrollMessage(it.interval + (it.scroller?.duration ?: 0))
                    }
                }
                else -> {}
            }
        }
    }


    inner class CustomDurationScroller @JvmOverloads constructor(context: Context, interpolator: Interpolator? = null) : Scroller(context, interpolator) {

        private var scrollFactor = 1.0

        /** Set the factor by which the duration will change*/
        fun setScrollDurationFactor(scrollFactor: Double) {
            this.scrollFactor = scrollFactor
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, (duration * scrollFactor).toInt())
        }
    }


    /**
     * *************************************************** Looping
     * *************************************
     */
    internal var mOuterPageChangeListener: OnPageChangeListener? = null
    private var mAdapter: LoopPagerAdapterWrapper? = null


    /**
     * helper function which may be used when implementing FragmentPagerAdapter
     *
     * @param position
     * @param count
     * @return (position-1)%count
     */
    fun toRealPosition(position: Int, count: Int): Int {
        val mPosition = position - 1
        return if (mPosition < 0) mPosition + count else mPosition % count
    }


    /**
     * If set to true, the boundary views (i.e. first and last) will never be
     * destroyed This may help to prevent "blinking" of some views
     *
     * @param mBoundaryCaching
     */
    fun setBoundaryCaching(mBoundaryCaching: Boolean) {
        mAdapter?.setBoundaryCaching(mBoundaryCaching)
    }

    override fun setAdapter(adapter: androidx.viewpager.widget.PagerAdapter?) {
        mAdapter = LoopPagerAdapterWrapper(adapter)
        super.setAdapter(mAdapter)
        setCurrentItem(0, false)
    }

    override fun getAdapter(): androidx.viewpager.widget.PagerAdapter? = mAdapter?.adapter ?: mAdapter

    override fun getCurrentItem(): Int = mAdapter?.toRealPosition(super.getCurrentItem()) ?: 0

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        val realItem = mAdapter?.toInnerPosition(item) ?: 0
        super.setCurrentItem(realItem, smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        if (currentItem != item) setCurrentItem(item, true)
    }

    override fun addOnPageChangeListener(listener: OnPageChangeListener) {
        mOuterPageChangeListener = listener
    }


    private val onPageChangeListener = object : OnPageChangeListener {
        private var mPreviousOffset = -1f
        private var mPreviousPosition = -1f

        override fun onPageSelected(position: Int) {

            val realPosition = mAdapter?.toRealPosition(position) ?: 0
            if (mPreviousPosition != realPosition.toFloat()) {
                mPreviousPosition = realPosition.toFloat()
                mOuterPageChangeListener?.onPageSelected(realPosition)
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            mAdapter?.let {

                val realPosition = it.toRealPosition(position)

                if (positionOffset == 0f && mPreviousOffset == 0f && (position == 0 || position == it.count)) {

                    if(!isCycle && realPosition != position)
                        mOuterPageChangeListener?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
                    else setCurrentItem(realPosition, false)
                }

            }

            this.mPreviousOffset = positionOffset

        }

        override fun onPageScrollStateChanged(state: Int) {

            mAdapter?.let {

                val position = super@AutoLoopViewPager.getCurrentItem()
                val realPosition = it.toRealPosition(position)

                if(state == SCROLL_STATE_IDLE && (position == 0 || position == it.count - 1)) {
                    if (!isCycle && realPosition != position)
                        mOuterPageChangeListener?.onPageScrollStateChanged(state)
                    else setCurrentItem(realPosition, false)
                }
            }

            mOuterPageChangeListener?.onPageScrollStateChanged(state)
        }
    }


    /**
     * A PagerAdapter wrapper responsible for providing a proper page to
     * LoopViewPager
     *
     *
     * This class shouldn't be used directly
     */
    class LoopPagerAdapterWrapper internal constructor(val adapter: androidx.viewpager.widget.PagerAdapter?) : androidx.viewpager.widget.PagerAdapter() {

        private var mToDestroy = SparseArray<ToDestroy>()
        private var mBoundaryCaching = false
        private var isCycle = true
        private val realFirstPosition: Int get() = if (isCycle) 1 else 0
        private val realLastPosition: Int get() = if (isCycle) realCount else realCount - 1
        private val realCount: Int get() = adapter?.count ?:0


        internal fun setBoundaryCaching(mBoundaryCaching: Boolean) {
            this.mBoundaryCaching = mBoundaryCaching
            super.notifyDataSetChanged()
        }

        internal fun setCycle(isCycle: Boolean) {
            this.isCycle = isCycle
            super.notifyDataSetChanged()
        }

        override fun notifyDataSetChanged() {
            mToDestroy = SparseArray<ToDestroy>()
            super.notifyDataSetChanged()
        }

        internal fun toRealPosition(position: Int): Int {

            var realPosition = position

            if (realCount == 0) return 0
            if (isCycle) {
                realPosition = (position - 1) % realCount
                if (realPosition < 0) realPosition += realCount
            }

            return realPosition
        }

        fun toInnerPosition(realPosition: Int): Int = if(isCycle) realPosition + 1 else realPosition

        override fun getCount(): Int = if (isCycle) realCount + 2 else realCount

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val realPosition =
                if (adapter is androidx.fragment.app.FragmentPagerAdapter || adapter is androidx.fragment.app.FragmentStatePagerAdapter) position
                else toRealPosition(position)

            if (mBoundaryCaching) {
                mToDestroy.get(position)?.let {
                    mToDestroy.remove(position)
                    return it.item
                }
            }

            return adapter?.instantiateItem(container, realPosition) ?: container
        }

        override fun destroyItem(container: ViewGroup, position: Int, item: Any) {

            val realPosition =
                if (adapter is androidx.fragment.app.FragmentPagerAdapter || adapter is androidx.fragment.app.FragmentStatePagerAdapter) position
                else toRealPosition(position)

            if (mBoundaryCaching && (position == realFirstPosition || position == realLastPosition)) {
                mToDestroy.put(position, ToDestroy(container, realPosition, item))
            }

            adapter?.destroyItem(container, realPosition, item)
        }

        /*
         * Delegate rest of methods directly to the inner adapter.
		 */
        override fun finishUpdate(container: ViewGroup) {
            adapter?.finishUpdate(container)
        }

        override fun isViewFromObject(view: View, item: Any): Boolean {
            return adapter?.isViewFromObject(view, item) ?: false
        }

        override fun restoreState(bundle: Parcelable?, classLoader: ClassLoader?) {
            adapter?.restoreState(bundle, classLoader)
        }

        override fun saveState(): Parcelable? {
            return adapter?.saveState()
        }

        override fun startUpdate(container: ViewGroup) {
            adapter?.startUpdate(container)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, item: Any) {
            adapter?.setPrimaryItem(container, position, item)
        }

    }

    /**
     * Container class for caching the boundary views
     */
    class ToDestroy(internal var container: ViewGroup,
                    internal var position: Int,
                    internal var item: Any)

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.stopAutoScroll()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAutoScroll) {
            this.startAutoScroll()
        }
    }
}