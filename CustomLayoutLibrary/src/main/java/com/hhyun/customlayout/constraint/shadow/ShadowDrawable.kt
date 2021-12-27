package com.hhyun.customlayout.constraint.shadow

import android.graphics.*
import android.graphics.drawable.Drawable

class ShadowDrawable(private var shadowBlurSize: Float, private var shadowX: Float, private var shadowY: Float,
                     private var shadowColor: Int,

                     private var bg: Int, private var radius: Float,
                     private var border: Int, private var borderSize: Float): Drawable() {

    companion object {
        internal const val DEFAULT_SHADOW_COLOR = Color.BLACK
        internal const val DEFAULT_SHADOW_BLUR_SIZE = 0f
        internal const val DEFAULT_SHADOW_X = 0f
        internal const val DEFAULT_SHADOW_Y = 0f
        internal const val DEFAULT_RADIUS = 0f
        internal const val DEFAULT_BG_COLOR = Color.WHITE
        internal const val DEFAULT_BORDER_COLOR = Color.BLACK
        internal const val DEFAULT_BORDER_SIZE = 0f
    }

    // shadow padding
    private var padding: Rect = Rect(0,0, 0, 0)

    // paint
    private var gradientPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var rectBg: RectF = RectF()
    private var rectGradient: RectF = RectF()

    constructor(): this(DEFAULT_SHADOW_BLUR_SIZE, DEFAULT_SHADOW_X, DEFAULT_SHADOW_Y, DEFAULT_SHADOW_COLOR, DEFAULT_BG_COLOR, DEFAULT_RADIUS, DEFAULT_BORDER_COLOR, DEFAULT_BORDER_SIZE)
    constructor(shadowBlurSize: Float, x: Float, y: Float): this(shadowBlurSize, x, y, DEFAULT_SHADOW_COLOR, DEFAULT_BG_COLOR, DEFAULT_RADIUS, DEFAULT_BORDER_COLOR, DEFAULT_BORDER_SIZE)


    init {
        padding.set((shadowBlurSize - shadowX).toInt(), (shadowBlurSize - shadowY).toInt(), (shadowBlurSize + shadowX).toInt(), (shadowBlurSize + shadowY).toInt())

//        if(shadowBlurSize > 0f)
//            gradientPaint.maskFilter = ShadowBlurMaskFilter.Builder().setRadius(shadowBlurSize).setStyle(BlurMaskFilter.Blur.NORMAL).build()

        if(shadowBlurSize > 0f)
            gradientPaint.maskFilter = BlurMaskFilter(shadowBlurSize, BlurMaskFilter.Blur.NORMAL)
    }



    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)

        if(bounds == null) {
            return
        }

        // size
        rectBg.set(shadowBlurSize - shadowX, shadowBlurSize - shadowY , bounds.right - shadowBlurSize - shadowX, bounds.bottom - shadowBlurSize - shadowY)
        rectGradient.set(shadowBlurSize, shadowBlurSize, bounds.right - shadowBlurSize, bounds.bottom - shadowBlurSize)
    }


    override fun draw(canvas: Canvas) {
        if(shadowBlurSize > 0f) {
            canvas.drawRoundRect(rectGradient, radius, radius, gradientPaint.apply {
                style = Paint.Style.FILL
                color = shadowColor
            })
        }

        // draw background
        canvas.drawRoundRect(rectBg , radius, radius, bgPaint.apply {
            color = bg
            style = Paint.Style.FILL
        })
        canvas.drawRoundRect(rectBg , radius, radius, bgPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = borderSize
            color = if(strokeWidth == 0f) bg else border
        })
    }

    override fun setAlpha(alpha: Int) {
        bgPaint.alpha = alpha
        gradientPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        bgPaint.colorFilter = colorFilter
        gradientPaint.colorFilter = colorFilter
    }


    override fun getPadding(padding: Rect): Boolean {
        padding.set(this.padding)
        return (padding.left or padding.top or padding.right or padding.bottom) != 0
    }


    class Builder {
        var shadowBlurSize: Float = DEFAULT_SHADOW_BLUR_SIZE
        var shadowX: Float = DEFAULT_SHADOW_X
        var shadowY: Float = DEFAULT_SHADOW_Y

        var shadowColor: Int = DEFAULT_SHADOW_COLOR

        var bg: Int = DEFAULT_BG_COLOR
        var radius: Float = DEFAULT_RADIUS

        var border: Int = DEFAULT_BORDER_COLOR
        var borderSize: Float = DEFAULT_BORDER_SIZE

        fun build() = ShadowDrawable(shadowBlurSize, shadowX, shadowY, shadowColor, bg, radius, border, borderSize)
    }
}