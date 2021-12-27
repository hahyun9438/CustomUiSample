package com.hhyun.customtextviewlibrary.separator

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hhyun.customtextviewlibrary.R
import com.hhyun.customtextviewlibrary.util.Util

/**
 * 대쉬 기호를 갖는 텍스트들을 한줄로 wrap하게 나열하는 커스텀 텍스트 뷰
 * 도레미 - 파솔라시도
 * 도레 - 미파솔라시도시라솔파미레도레...
 * 도레미파솔라시... - 도레미파솔라시...
 * 도레미파솔라시도시라솔파미레도 - 도레미
 * 도레미 - 파솔라 - 시도시라 - 라솔파미
 */
class SeparatorLinkedWrapTextView: LinearLayout {

    companion object {

        private const val TAG = "SldTextView"

        private const val DASH = " - "

        const val GRAVITY_START = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_END = 2

        const val ELLIPSIZE_END = 0
        const val ELLIPSIZE_MIDDLE = 1

        private const val DASH_MIN_WIDTH = 20

        private const val UN_NECESSARY_RESIZE = 100
        private const val NECESSARY_RESIZE = 200
    }


    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(attrs, defStyleAttr) }

    private var fullTextView: TextView? = null
    private var unitTextCount: Int = 0
    private var unitAbleWidth: Int = 0

    private var dashCount: Int = 0
    private var dashWidth: Int = DASH_MIN_WIDTH

    private var mText: CharSequence = ""
    private var mTextSize: Int = 15
    private var mTextColor: Int = R.color.hhyunctlib_black
    private var mGravity: Int = GRAVITY_START
    private var mEllipsize: Int = ELLIPSIZE_END

    private var textList = arrayListOf<CharSequence>()
    private var unitTextList = arrayListOf<String>()
    private var finalTextTypeList = arrayListOf<Int>()


    init {
        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.orientation = HORIZONTAL
        this.gravity = Gravity.CENTER
    }

    private fun init(attrs: AttributeSet? = null, defStyleAttr: Int? = null) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleLineDashWrapTextView)
        try {
            this.mGravity = typedArray.getInteger(R.styleable.SingleLineDashWrapTextView_sld_gravity, GRAVITY_START)
            this.mText = typedArray.getText(R.styleable.SingleLineDashWrapTextView_sld_text) ?: ""
            this.mTextSize = typedArray.getInteger(R.styleable.SingleLineDashWrapTextView_sld_text_size, 15)
            this.mTextColor = typedArray.getResourceId(R.styleable.SingleLineDashWrapTextView_sld_text_color, R.color.hhyunctlib_black)
            this.mEllipsize = typedArray.getInteger(R.styleable.SingleLineDashWrapTextView_sld_ellipsize, ELLIPSIZE_END)

        } finally {
            typedArray.recycle()
        }

        getDashWidth()
    }

    private fun getDashWidth() {

        this.unitTextList.clear()
        this.unitTextList.addAll(mText.split(DASH))
        this.unitTextCount = unitTextList.size
        this.dashCount = unitTextCount - 1

        if(dashCount <= 0) {
            setView()
            return
        }

        dashWidth = Util.getTextWidth(context, DASH, mTextSize).toInt()
        setView()

    }

    private fun setView() {

        if(mText.isEmpty()) return

        removeAllViews()

        if(mGravity != GRAVITY_CENTER) {
            this.gravity = if(mGravity == GRAVITY_START) Gravity.START else Gravity.END
        }

        fullTextView = makeTextView(ViewGroup.LayoutParams.MATCH_PARENT, mText)

        if(dashCount <= 0) {
            fullTextView?.let { addView(it) }
            return
        }

        fullTextView?.let { tv ->

            addView(tv)

            tv.post {

                tv.layout?.let {

                    textList.clear()

                    for (i in 0 until it.lineCount) {
                        val start = if(i == 0) 0 else it.getLineEnd(i - 1)
                        val end = it.getLineEnd(i)
                        val splitContext = tv.text.subSequence(start, end)
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

        val isOverSingleLine = textList.size > 1
        if(!isOverSingleLine) {
            return
        }

        /**
         * 한 항목이 가질 수 있는 최대 너비는
         * [전체 레이아웃 너비에서 대쉬기호(-)들의 너비 합을 뺀 너비]를 항목의 갯수만큼 균등하게 분할한 사이즈임
         */
        this.unitAbleWidth = (this.measuredWidth - (dashWidth * dashCount)) / unitTextCount

        removeAllViews()
        finalTextTypeList.clear()


        unitTextList.forEach { textItem ->
            val width = Util.getTextWidth(context, textItem, mTextSize)
            finalTextTypeList.add(if(width > unitAbleWidth) NECESSARY_RESIZE else UN_NECESSARY_RESIZE)
        }

        setFinalTextView()

    }

    private fun setFinalTextView() {

        /**
         * 텍스트뷰를 리사이징 하지 않아도 된다 == 텍스트 너비가 가능한 최대 너비보다 짧다
         */

        val isLastTextUnResize = finalTextTypeList.lastOrNull() == UN_NECESSARY_RESIZE  // 마지막 항목이 리사이징을 안해도 되는 항목인지 여부
        var isJustBeforeWeightView = false  // 직전 항목이 weight view 인지 여부

        removeAllViews()
        finalTextTypeList.forEachIndexed { index, type ->

            val isFirst = index == 0                                        // 첫번재 항목인지 여부
            val isOneBeforeLast = index == finalTextTypeList.size - 2       // 마지막의 바로 전 항목인지 여부
            val isLast = index == finalTextTypeList.size - 1                // 마지막 항목인지 여부

            val text = unitTextList.getOrNull(index)

            when {
                isFirst && isOneBeforeLast && isLastTextUnResize -> {
                    /**
                     * [>>>>>> weight >>>>>>] - wrap
                     * 전체 항목이 2개일때 첫번째 항목이면서 마지막 항목이 리사이징을 하지 않아도 되는 경우
                     * -> 첫번째 항목을 가능한 최대 너비로 셋팅 후, 말줄임 처리
                     */
                    addView(makeWeightTextView(text))
                    isJustBeforeWeightView = true
                }

                !isJustBeforeWeightView && isLast -> {
                    /**
                     * ... - wrap - [>>> weight >>>]
                     * 직전 항목이 리사이징을 하지 않아도 되는 항목이면서 현재 마지막 아이템인 경우
                     * -> 마지막 항목을 가능한 최대 너비로 셋팅 후, 말줄임 처리
                     */
                    addView(makeWeightTextView(text))
                    isJustBeforeWeightView = false
                }

                type == UN_NECESSARY_RESIZE -> { // 현재 항목이 라사이징을 해야 하는 항목인 경우
                    /**
                     * ... - wrap
                     * 현재 항목이 리사이징을 하지 않아도 되는 경우
                     * -> 텍스트 너비 만큼 셋팅
                     */
                    addView(makeTextView(ViewGroup.LayoutParams.WRAP_CONTENT, text, true))
                    isJustBeforeWeightView = false
                }

                else -> {
                    /**
                     * ... - [-         -]
                     * 그 외
                     * -> 가능한 최대 너비로 셋팅한 후, 말줄임 처리
                     */
                    addView(makeTextView(unitAbleWidth, text, true))
                    isJustBeforeWeightView = false
                }
            }

            if(!isLast) {
                addView(makeTextView(ViewGroup.LayoutParams.WRAP_CONTENT, DASH))
            }

        }
    }

    private fun makeTextView(width: Int, text: CharSequence?, isSingleLine: Boolean = false): TextView {
        return TextView(context).apply {
            this.layoutParams = LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            val param = this.layoutParams as LinearLayout.LayoutParams
            param.gravity = Gravity.CENTER_VERTICAL

            this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
            this.setTextColor(ContextCompat.getColor(context, mTextColor))
            this.text = text ?: ""

            if(isSingleLine) {
                setSingleLine()
                ellipsize = when(mEllipsize) {
                    ELLIPSIZE_END -> TextUtils.TruncateAt.END
                    ELLIPSIZE_MIDDLE -> TextUtils.TruncateAt.MIDDLE
                    else -> TextUtils.TruncateAt.END
                }
            }
        }
    }

    private fun makeWeightTextView(text: CharSequence?): TextView {
        return TextView(context).apply {
            this.layoutParams = LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

            val param = this.layoutParams as LinearLayout.LayoutParams
            param.gravity = Gravity.CENTER_VERTICAL

            this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
            this.setTextColor(ContextCompat.getColor(context, mTextColor))
            this.text = text ?: ""

            setSingleLine()
            ellipsize = when(mEllipsize) {
                ELLIPSIZE_END -> TextUtils.TruncateAt.END
                ELLIPSIZE_MIDDLE -> TextUtils.TruncateAt.MIDDLE
                else -> TextUtils.TruncateAt.END
            }
        }
    }




    fun setView(gravity: Int,
                text: String,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                ellipsize: Int = ELLIPSIZE_END) {

        if(text.isEmpty()) return
        if(isEqule(gravity, text, textSize, textColor, ellipsize)) return

//        Log.d(TAG, "setView text = $text")

        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mEllipsize = ellipsize
        getDashWidth()
    }

    fun setView(gravity: Int,
                text: SpannableString,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                ellipsize: Int = ELLIPSIZE_END) {

        if(text.isEmpty()) return
        if(isEqule(gravity, text, textSize, textColor, ellipsize)) return

//        Log.d(TAG, "setView text = $text")

        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mEllipsize = ellipsize
        getDashWidth()
    }

    fun setView(gravity: Int,
                text: CharSequence,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                ellipsize: Int = ELLIPSIZE_END) {

        if(text.isEmpty()) return
        if(isEqule(gravity, text, textSize, textColor, ellipsize)) return

//        Log.d(TAG, "setView text = $text")

        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mEllipsize = ellipsize
        getDashWidth()
    }


    private fun isEqule(gravity: Int,
                        text: String,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        ellipsize: Int = ELLIPSIZE_END): Boolean {
        return mText == text
                && isEqule(gravity, textSize, textColor, ellipsize)
    }

    private fun isEqule(gravity: Int,
                        text: SpannableString,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        ellipsize: Int = ELLIPSIZE_END): Boolean {
        return mText == text
                && isEqule(gravity, textSize, textColor, ellipsize)
    }

    private fun isEqule(gravity: Int,
                        text: CharSequence,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        ellipsize: Int = ELLIPSIZE_END): Boolean {
        return mText == text
                && isEqule(gravity, textSize, textColor, ellipsize)
    }

    private fun isEqule(gravity: Int,
                        textSize: Int = 15,
                        textColor: Int = R.color.hhyunctlib_black,
                        ellipsize: Int = ELLIPSIZE_END): Boolean {
        return mGravity == gravity
                && mTextSize == textSize
                && mTextColor == textColor
                && mEllipsize == ellipsize
    }
}