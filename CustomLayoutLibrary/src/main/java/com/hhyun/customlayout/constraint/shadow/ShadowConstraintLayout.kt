package com.hhyun.customlayout.constraint.shadow

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.hhyun.customlayout.R

class ShadowConstraintLayout: ConstraintLayout, ShadowView {

    override val originPadding: Rect = Rect()
    override var showShadow: Boolean = true

    private var makeShadowDrawable: ShadowDrawable? = null
    private var initPaddingLeft = 0
    private var initPaddingRight = 0
    private var initPaddingTop = 0
    private var initPaddingBottom = 0

    private var backgroundPaddingLeft = 0
    private var backgroundPaddingRight = 0
    private var backgroundPaddingTop = 0
    private var backgroundPaddingBottom = 0


    constructor(context: Context) : super(context) { init(context, null, 0) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(context, attrs, 0) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(context, attrs, defStyleAttr) }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        initOriginPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        this.makeShadowDrawable = makeShadowDrawable(context, attrs, this)
        this.background = makeShadowDrawable

        val backgroundPadding = Rect().apply { background.getPadding(this) }
        this.backgroundPaddingLeft = backgroundPadding.left
        this.backgroundPaddingTop = backgroundPadding.top
        this.backgroundPaddingRight = backgroundPadding.right
        this.backgroundPaddingBottom = backgroundPadding.bottom

        optimizePadding()
        setVisibilityShadow(showShadow)
    }


    override fun optimizePadding() {
        this.initPaddingLeft = originPadding.left + backgroundPaddingLeft
        this.initPaddingTop = originPadding.top + backgroundPaddingTop
        this.initPaddingRight = originPadding.right + backgroundPaddingRight
        this.initPaddingBottom = originPadding.bottom + backgroundPaddingBottom

        setPadding(initPaddingLeft, initPaddingTop, initPaddingRight, initPaddingBottom)
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        optimizePadding()
    }

    override fun setBackgroundDrawable(background: Drawable?) {
        super.setBackgroundDrawable(background)
        optimizePadding()
    }

    override fun setBackgroundColor(color: Int) {
//        super.setBackgroundColor(colors.xml)
    }

    override fun setBackgroundResource(resId: Int) {
//        super.setBackgroundResource(resId)
    }

    fun setVisibilityShadow(isShow: Boolean) {

        if(isShow) {
            setPadding(0, 0, 0, 0)
            this.background = makeShadowDrawable

        } else {
            this.background = ContextCompat.getDrawable(context, R.drawable.hhyuncllib_transparent).apply {
                setPadding(initPaddingLeft, initPaddingTop, initPaddingRight, initPaddingBottom)
            }
        }
    }
}