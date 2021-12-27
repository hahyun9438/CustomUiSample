package com.hhyun.customtextviewlibrary.drawable

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hhyun.customtextviewlibrary.R
import com.hhyun.customtextviewlibrary.util.Util

class EndDrawableWrapTextView: LinearLayout {

    companion object {

        private const val TAG = "EdwTextView"

        const val GRAVITY_START = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_END = 2
    }


    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(attrs, defStyleAttr) }

    private var startTextView: TextView? = null

    private var mText: CharSequence = ""
    private var mTextSize: Int = 15
    private var mTextColor: Int = R.color.hhyunctlib_black
    private var mDrawable: Int? = null
    private var mDrawablePadding: Int = 0
    private var mGravity: Int = GRAVITY_START

    private var textList = arrayListOf<CharSequence>()


    init {
        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.orientation = VERTICAL
        this.gravity = Gravity.CENTER
    }

    private fun init(attrs: AttributeSet? = null, defStyleAttr: Int? = null) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EndDrawableWrapTextView)
        try {
            this.mGravity = typedArray.getInteger(R.styleable.EndDrawableWrapTextView_edw_gravity, GRAVITY_START)
            this.mText = typedArray.getText(R.styleable.EndDrawableWrapTextView_edw_text) ?: ""
            this.mTextSize = typedArray.getInteger(R.styleable.EndDrawableWrapTextView_edw_text_size, 15)
            this.mTextColor = typedArray.getResourceId(R.styleable.EndDrawableWrapTextView_edw_text_color, R.color.hhyunctlib_black)
            this.mDrawable = typedArray.getResourceId(R.styleable.EndDrawableWrapTextView_edw_drawable, 0)
            this.mDrawablePadding = typedArray.getInteger(R.styleable.EndDrawableWrapTextView_edw_drawable_padding, 0)

        } finally {
            typedArray.recycle()
        }

        setView()
    }


    private fun setView() {

        if(mText.isEmpty()) return

        removeAllViews()

        if(mGravity != GRAVITY_CENTER) {
            this.gravity = if(mGravity == GRAVITY_START) Gravity.START else Gravity.END
        }

        this.startTextView = null
        this.startTextView = TextView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        startTextView?.text = mText
        startTextView?.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
        startTextView?.setTextColor(ContextCompat.getColor(context, mTextColor))
        mDrawable?.takeIf { it > 0 }?.let {
            val icon = ContextCompat.getDrawable(context, it) ?: return
            startTextView?.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
            startTextView?.compoundDrawablePadding = Util.dpToPx(context, mDrawablePadding)
        }

        startTextView?.let { tv ->

            addView(tv)

            tv.post {

                tv.layout?.let {

                    textList.clear()

                    for (i in 0 until it.lineCount) {
                        val start = if(i == 0) 0 else it.getLineEnd(i - 1)
                        val end = it.getLineEnd(i)
                        val splitContext = startTextView!!.text.subSequence(start, end)
                        textList.add(splitContext)
                    }

                    if(textList.isNotEmpty()) {
                        setTextView()
                    }

                }

            }

        }
    }

    private fun setTextView() {

        removeAllViews()

        textList.forEachIndexed { index, s ->

            if(s.isEmpty()) return@forEachIndexed

            val textView = TextView(context).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            textView.text = s
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
            textView.setTextColor(ContextCompat.getColor(context, mTextColor))
            textView.setSingleLine()

            if(index == textList.size - 1 && mDrawable != null) {
                val icon = ContextCompat.getDrawable(context, mDrawable!!)
                textView.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                textView.compoundDrawablePadding = Util.dpToPx(context, mDrawablePadding)
            }

            addView(textView)
        }

    }

    fun setView(gravity: Int,
                text: String,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                drawable: Int? = null,
                drawablePadding: Int = 0) {

        if(text.isEmpty()) return
        if(isEqule(gravity, text, textSize, textColor, drawable, drawablePadding)) return

//        Log.d(TAG, "setView text = $text")

        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mDrawable = drawable
        this.mDrawablePadding = drawablePadding
        setView()
    }

    fun setView(gravity: Int,
                text: SpannableString,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                drawable: Int? = null,
                drawablePadding: Int = 0) {

        if(text.isEmpty()) return
        if(isEqule(gravity, text, textSize, textColor, drawable, drawablePadding)) return

//        Log.d(TAG, "setView text = $text")

        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mDrawable = drawable
        this.mDrawablePadding = drawablePadding
        setView()
    }

    fun setView(gravity: Int,
                text: CharSequence,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                drawable: Int? = null,
                drawablePadding: Int = 0) {

        if(isEqule(gravity, text, textSize, textColor, drawable, drawablePadding)) return

//        Log.d(TAG, "setView text = $text")

        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mDrawable = drawable
        this.mDrawablePadding = drawablePadding
        setView()
    }


    fun setText(text: String) {
        this.mText = text
        setView()
    }

    fun setText(text: SpannableString) {
        this.mText = text
        setView()
    }

    fun setText(text: CharSequence) {
        this.mText = text
        setView()
    }



    private fun isEqule(gravity: Int,
                        text: String,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        drawable: Int? = null,
                        drawablePadding: Int = 0): Boolean {
        return mText == text
                && isEqule(gravity, textSize, textColor, drawable, drawablePadding)
    }

    private fun isEqule(gravity: Int,
                        text: SpannableString,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        drawable: Int? = null,
                        drawablePadding: Int = 0): Boolean {
        return mText == text
                && isEqule(gravity, textSize, textColor, drawable, drawablePadding)
    }

    private fun isEqule(gravity: Int,
                        text: CharSequence,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        drawable: Int? = null,
                        drawablePadding: Int = 0): Boolean {
        return mText == text
                && isEqule(gravity, textSize, textColor, drawable, drawablePadding)
    }

    private fun isEqule(gravity: Int,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        drawable: Int? = null,
                        drawablePadding: Int = 0): Boolean {
        return mGravity == gravity
                && mTextSize == textSize
                && mTextColor == textColor
                && mDrawable == drawable
                && mDrawablePadding == drawablePadding
    }

}