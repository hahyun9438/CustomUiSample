package com.hhyun.customlayout.constraint.round

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.hhyun.customlayout.R

class RoundConstraintLayout: ConstraintLayout {

    protected var mRadius = 0f
    protected var mRadiusLeftTop = 0f
    protected var mRadiusLeftBottom = 0f
    protected var mRadiusRightTop = 0f
    protected var mRadiusRightBottom = 0f
    protected var path: Path? = null
    protected var rect: RectF? = null
    protected var paint: Paint? = null

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr)
    }

    protected open fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val a: TypedArray = this.getContext().obtainStyledAttributes(attrs, R.styleable.RoundConstraintLayout)
            mRadius = a.getDimensionPixelSize(R.styleable.RoundConstraintLayout_rcl_radius, 0).toFloat()
            mRadiusLeftTop = a.getDimensionPixelSize(R.styleable.RoundConstraintLayout_rcl_radius_left_top, 0).toFloat()
            mRadiusLeftBottom = a.getDimensionPixelSize(R.styleable.RoundConstraintLayout_rcl_radius_left_bottom, 0).toFloat()
            mRadiusRightTop = a.getDimensionPixelSize(R.styleable.RoundConstraintLayout_rcl_radius_right_top, 0).toFloat()
            mRadiusRightBottom = a.getDimensionPixelSize(R.styleable.RoundConstraintLayout_rcl_radius_right_bottom, 0).toFloat()
        }
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        try {
            val count = canvas.save()
            if (path != null) {
                canvas.clipPath(path!!)
            }
            super.dispatchDraw(canvas)
            canvas.restoreToCount(count)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect = RectF(0f, 0f, w.toFloat(), h.toFloat())
        path = Path()

        if (mRadius > 0) setAllRound() else setRound()
    }

    private fun setAllRound() {
        if(rect != null) {
            path?.addRoundRect(rect!!, mRadius, mRadius, Path.Direction.CW)
        }
    }

    private fun setRound() {
        if(rect != null) {
            val radii = FloatArray(8)
            radii[0] = mRadiusLeftTop
            radii[1] = mRadiusLeftTop
            radii[2] = mRadiusRightTop
            radii[3] = mRadiusRightTop
            radii[4] = mRadiusRightBottom
            radii[5] = mRadiusRightBottom
            radii[6] = mRadiusLeftBottom
            radii[7] = mRadiusLeftBottom
            path?.addRoundRect(rect!!, radii, Path.Direction.CW)
        }
    }


    fun setRadius(radius: Float) {
        this.mRadius = radius
        setAllRound()
        invalidate()
    }

    fun setLeftTopRadius(radiusLeftTop: Float) {
        this.mRadiusLeftTop = radiusLeftTop
        setRound()
        invalidate()
    }

    fun setLeftBottomRadius(radiusLeftBottom: Float) {
        this.mRadiusLeftBottom = radiusLeftBottom
        setRound()
        invalidate()
    }

    fun setRightTopRadius(radiusRightTop: Float) {
        this.mRadiusRightTop = radiusRightTop
        setRound()
        invalidate()
    }

    fun setRightBottomRadius(radiusRightBottom: Float) {
        this.mRadiusRightBottom = radiusRightBottom
        setRound()
        invalidate()
    }
}