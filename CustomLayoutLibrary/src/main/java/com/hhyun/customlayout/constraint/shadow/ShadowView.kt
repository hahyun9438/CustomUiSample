package com.hhyun.customlayout.constraint.shadow

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.hhyun.customlayout.R

interface ShadowView {
    // view's original padding
    val originPadding: Rect
    var showShadow: Boolean

    fun initOriginPadding(left: Int, top: Int, right: Int, bottom: Int) {
        originPadding.left = left
        originPadding.top = top
        originPadding.right = right
        originPadding.bottom = bottom
    }

    fun getOriginPaddingTop(): Int {
        return originPadding.top
    }

    fun getOriginPaddingBottom(): Int {
        return originPadding.bottom
    }

    fun getOriginPaddingLeft(): Int {
        return originPadding.left
    }

    fun getOriginPaddingRight(): Int {
        return originPadding.right
    }

    fun optimizePadding()


    fun makeShadowDrawable(context: Context, attrs: AttributeSet?, view: View): ShadowDrawable {

        val shadowBuilder = ShadowDrawable.Builder()

        if (attrs != null) {

            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.ShadowLayout
            )

            a.getDimension(R.styleable.ShadowLayout_shadowBlurSize, -1f)
                .takeIf { it >= 0f }?.apply { shadowBuilder.shadowBlurSize = this }

            a.getDimension(R.styleable.ShadowLayout_shadowX, 0f)
                .takeIf { it >= 0f }?.apply { shadowBuilder.shadowX = this }

            a.getDimension(R.styleable.ShadowLayout_shadowY, 0f)
                .takeIf { it >= 0f }?.apply { shadowBuilder.shadowY = this }

            a.getColor(R.styleable.ShadowLayout_shadowColor, 0)
                .takeIf { it != 0 }?.apply { shadowBuilder.shadowColor = this }

            a.getColor(R.styleable.ShadowLayout_shadowBgColor, 0)
                .takeIf { it != 0 }?.apply { shadowBuilder.bg = this }

            a.getDimension(R.styleable.ShadowLayout_shadowBgRadius, 0f)
                .takeIf { it >= 0f }?.apply { shadowBuilder.radius = this }

            a.getColor(R.styleable.ShadowLayout_shadowBorderColor, 0)
                .takeIf { it != 0 }?.apply { shadowBuilder.border = this }

            a.getDimension(R.styleable.ShadowLayout_shadowBorderSize, 0f)
                .takeIf { it >= 0f }?.apply { shadowBuilder.borderSize = this }

            showShadow = a.getBoolean(R.styleable.ShadowLayout_shadowShow, true)

            a.recycle()
        }

        return shadowBuilder.build()
    }

}