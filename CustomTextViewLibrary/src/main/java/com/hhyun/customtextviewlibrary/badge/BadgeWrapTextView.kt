package com.hhyun.customtextviewlibrary.badge

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.flexbox.JustifyContent
import com.hhyun.customtextviewlibrary.CharacterWrapTextView
import com.hhyun.customtextviewlibrary.R
import com.hhyun.customtextviewlibrary.util.Util

abstract class BadgeWrapTextView: LinearLayout {

    companion object {

        const val TAG = "BadgeWrapTextView"

        const val GRAVITY_START = 0
        const val GRAVITY_CENTER = 1
        const val GRAVITY_END = 2

        enum class BadgeWrapType() {
            START, MIDDLE, END
        }

        const val MAX_LINES = 2000
    }


    constructor(context: Context) : super(context) { baseInit() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { baseInit(attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { baseInit(attrs, defStyleAttr) }

    protected var mMaxLine: Int = MAX_LINES

    protected var mText: CharSequence = ""
    protected var mTextSize: Int = 15
    protected var mTextColor: Int = R.color.hhyunctlib_black
    protected var mGravity: Int = GRAVITY_START
    protected var mLeftMargin: Int = 0
    protected var mRightMargin: Int = 0
    protected var mLeftPadding: Int = 0
    protected var mRightPadding: Int = 0

    protected var mBadgeList: ArrayList<BadgeData> = arrayListOf()

    protected var mLineMargin: Int = 0

    protected var textList = arrayListOf<CharSequence>()
    private var mTextContainerWidth = 0f

    abstract var badgeWrapType: BadgeWrapType


    init {
        this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.orientation = VERTICAL
        this.gravity = Gravity.CENTER
    }


    private fun baseInit(attrs: AttributeSet? = null, defStyleAttr: Int? = null) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HhyunBadgeWrapTextView)
        try {
            this.mMaxLine = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_max_line, MAX_LINES)
            this.mGravity = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_gravity, GRAVITY_START)
            this.mText = typedArray.getText(R.styleable.HhyunBadgeWrapTextView_text) ?: ""
            this.mTextSize = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_text_size, 15)
            this.mTextColor = typedArray.getResourceId(
                R.styleable.HhyunBadgeWrapTextView_text_color,
                R.color.hhyunctlib_black
            )

            this.mLineMargin = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_line_margin, 0)

            this.mLeftMargin = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_left_margin, 0)
            this.mRightMargin = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_right_margin, 0)
            this.mLeftPadding = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_left_padding, 0)
            this.mRightPadding = typedArray.getInteger(R.styleable.HhyunBadgeWrapTextView_right_padding, 0)

        } catch (e: Exception) {
            e.printStackTrace()

        } finally {
            typedArray.recycle()
        }

        setView()
    }


    abstract fun setTextView()
    open fun setView() {
        this.gravity = getParentGravity()
    }



    //-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
    // 뷰 getter
    //-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
    /** 텍스트의 너비를 계산하기 위해 임시로 생성하는 초기 텍스트뷰 */
    protected fun getTempTextView(tempText: CharSequence?, fixWidth: Float? = null): TextView {
        return TextView(context).apply {
            this.layoutParams = if(fixWidth != null && fixWidth > 0) LayoutParams(fixWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
            else LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            this.text = tempText ?: ""
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
            setTextColor(ContextCompat.getColor(context, R.color.hhyunctlib_transparent))
        }
    }

    /** 텍스트뷰 */
    protected fun getTextView(mText: CharSequence?, isSingleLine: Boolean? = true): TextView? {
        var textView: TextView? = null
        mText?.takeIf { it.isNotEmpty() }?.let {
            textView = TextView(context).apply {
                this.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    this.gravity = getParentGravity()
                }

                this.text = it
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
                setTextColor(ContextCompat.getColor(context, mTextColor))
                this.includeFontPadding = false

                if(isSingleLine == true) {
                    setSingleLine()
                }
            }
        }
        return textView
    }

    /** 텍스트뷰 */
    protected fun getCharacterWrapTextView(mText: CharSequence?, maxWidth: Int): TextView? {
        var textView: CharacterWrapTextView? = null
        mText?.takeIf { it.isNotEmpty() }?.let {
            textView = CharacterWrapTextView(context).apply {
                this.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    this.gravity = getParentGravity()
                    this.weight = 1f
                }

                this.maxWidth = maxWidth

                this.text = it
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
                setTextColor(ContextCompat.getColor(context, mTextColor))
                this.includeFontPadding = false

                this.maxLines = 1
                this.ellipsize = TextUtils.TruncateAt.END
                this.gravity = Gravity.CENTER_VERTICAL
            }
        }
        return textView
    }

    /** 뱃지뷰 */
    private fun getBadgeView(badge: BadgeData?): View? {
        if(badge == null) return null
        if(badge.labelText.isNullOrEmpty() && badge.imageResId == null) return null

        val badgeView = LinearLayout(context).apply {
            layoutParams = when {
                badge.width != null && badge.width!! > 0
                        && badge.height != null && badge.height!! > 0 -> LayoutParams(Util.dpToPx(context, badge.width!!), Util.dpToPx(context, badge.height!!))
                badge.width != null && badge.width!! > 0 -> LayoutParams(Util.dpToPx(context, badge.width!!), ViewGroup.LayoutParams.WRAP_CONTENT)
                badge.height != null && badge.height!! > 0 -> LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, Util.dpToPx(context, badge.height!!))
                else -> LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
        }

        var badgeTextView: TextView? = null
        if(!badge.labelText.isNullOrEmpty()) {
            badgeTextView = TextView(context).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            badgeTextView.text = badge.labelText
            badgeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (badge.labelTextSize ?: 11).toFloat())
            badgeTextView.setTextColor(ContextCompat.getColor(context, badge.labelTextColor ?: R.color.hhyunctlib_black))
            badgeTextView.setSingleLine()
            badgeTextView.gravity = Gravity.CENTER
            badgeTextView.includeFontPadding = false
        }

        var badgeImageView: ImageView? = null
        if(badge.imageResId != null) {
            badgeImageView = ImageView(context).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            badgeImageView.setImageDrawable(ContextCompat.getDrawable(context, badge.imageResId!!))
            badgeImageView.adjustViewBounds = true
        }

        badge.background?.let { badgeView.setBackgroundResource(it) }

        badgeView.setPadding(
            Util.dpToPx(context, badge.leftPadding),
            Util.dpToPx(context, badge.topPadding),
            Util.dpToPx(context, badge.rightPadding),
            Util.dpToPx(context, badge.bottomPadding))

        badgeTextView?.let { badgeView.addView(it) }
        badgeImageView?.let { badgeView.addView(it) }

        return badgeView
    }

    /** 뱃지로만 이루어진 라인뷰 */
    protected fun getBadgeLineView(badgeList: List<BadgeData>?): LinearLayout {
        val badgeLineView = LinearLayout(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        badgeList?.forEachIndexed { index, badge ->

            val isFirst = index == 0
            val isLast = index == badgeList.size - 1

            getBadgeView(badge)?.let {

                if(badgeWrapType == BadgeWrapType.START && isFirst) {
                    badgeLineView.addView(getGapView(1))
                }

                badgeLineView.addView(it)

                if(!isLast) {
                    badgeLineView.addView(getGapView(badge.gapMargin))
                }

                if(badgeWrapType == BadgeWrapType.END && isLast) {
                    badgeLineView.addView(getGapView(1))
                }
            }
        }

        return badgeLineView
    }

    /** 라인과 라인 사이(vertical)의 공백뷰 */
    protected fun getLineView(): View {
        return View(context).apply {
            val size = Util.dpToPx(context, mLineMargin)
            layoutParams = LayoutParams(size, size)
        }
    }

    /** 뷰와 뷰 사이(horizontal)의 공백뷰 */
    protected fun getGapView(width: Int): View {
        return View(context).apply {
            val size = Util.dpToPx(context, width)
            layoutParams = LayoutParams(size, size)
            gravity = Gravity.CENTER_VERTICAL
        }
    }




    //-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
    // 너비 / 길이 계산
    //-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
    /**
     * 해당 루트뷰가 가질 수 있는 너비 반환
     * 뷰가 그려지기 전에 너비를 반환해야하는 경우에는 패딩과 마진 값을 받아서 직접 계산한다.
     * */
    protected fun getTextContainerWidth(): Float {
        if(mTextContainerWidth > 0) return mTextContainerWidth
        var mMeasureWidth = this.measuredWidth.toFloat()
        if(mMeasureWidth <= 0) {
            val marginLayoutParam = this.layoutParams as MarginLayoutParams
            val leftMargin = if(mLeftMargin > 0) Util.dpToPx(context, mLeftMargin) else marginLayoutParam.leftMargin
            val rightMargin = if(mRightMargin > 0) Util.dpToPx(context, mRightMargin) else marginLayoutParam.rightMargin
            val leftPadding = if(mLeftPadding > 0) Util.dpToPx(context, mLeftPadding) else paddingLeft
            val rightPadding = if(mRightPadding > 0) Util.dpToPx(context, mRightPadding) else paddingRight
            val horizontalMargin = leftMargin + rightMargin
            val horizontalPadding = leftPadding + rightPadding
            mMeasureWidth = (Util.getDeviceWidth(context) - horizontalMargin - horizontalPadding).toFloat()
        }
        return mMeasureWidth.also { mTextContainerWidth = it }
    }

    /** 텍스트만을 가지는 텍스트뷰의 너비 반환 */
    protected fun getTextWidth(text: CharSequence?): Float {
        return (Util.getTextWidth(context, text, mTextSize))
    }

    /** 한 라인에 들어가는 뱃지의 총 너비를 제외한 텍스트뷰만 가질 수 있는 최대 너비 반환 */
    protected fun getTextMaxWidthExcludingBadge(badgeList: List<BadgeData>?): Float {
        return (getTextContainerWidth() - getBadgeTotalWidth(badgeList))
    }

    /** 한 라인에 들어가는 뱃지의 총 너비 반환 */
    private fun getBadgeTotalWidth(badgeList: List<BadgeData>?): Float {
        var totalBadgeWidth = 0f
        badgeList?.forEach { totalBadgeWidth += getBadgeWidth(it) }
        return totalBadgeWidth
    }

    /** 특정 뱃지의 너비 반환 */
    protected fun getBadgeWidth(badge: BadgeData?): Float {
        if(badge == null) return 0f

        val hasData = badge.isValidBadge

        val leftPadding = Util.dpToPx(context, badge.leftPadding)
        val rightPadding = Util.dpToPx(context, badge.rightPadding)
        val margin = Util.dpToPx(context, badge.gapMargin)

        val bodyWidth = when {
            hasData && (badge.width ?: 0) > 0 -> Util.dpToPx(context, badge.width!!).toFloat()
            badge.badgeType == BadgeType.TEXT -> Util.getTextWidth(context, badge.labelText, badge.labelTextSize)
            badge.badgeType == BadgeType.IMAGE -> ContextCompat.getDrawable(context, badge.imageResId)?.intrinsicWidth?.toFloat() ?: 0f
            else -> 0f
        }

        return (bodyWidth + leftPadding + rightPadding + margin)
    }

    /**
     * 한 라인에 텍스트와 뱃지를 모두 넣었을때 텍스트가 잘리는지 여부 반환
     * 텍스트가 잘리는 않는 경우에는 상황에 따라 FlexboxLayout에 뷰를 넣어야 할 수 있기 때문에 계산한다.
     */
    protected fun isNotEnoughWidthWithBadge(text: CharSequence?, badgeList: ArrayList<BadgeData>?): Boolean {
        return (getTextMaxWidthExcludingBadge(badgeList) < getTextWidth(text))
    }


    abstract fun getBadgeLineList(): ArrayList<ArrayList<BadgeData>>


    protected fun getParentGravity(): Int {
        return when(mGravity) {
            GRAVITY_START -> Gravity.CENTER_VERTICAL or Gravity.START
            GRAVITY_END -> Gravity.CENTER_VERTICAL or Gravity.END
            else -> Gravity.CENTER
        }
    }

    protected fun getFlexJustifyContent(): Int {
        return when(mGravity) {
            GRAVITY_START -> JustifyContent.FLEX_START
            GRAVITY_END -> JustifyContent.FLEX_END
            else -> JustifyContent.CENTER
        }
    }


    protected fun CharSequence?.trim(): CharSequence {
        if (this == null || this.isEmpty()) {
            return ""
        }

        return this.filter { s -> !s.isWhitespace() }
    }



    //-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
    // getter / setter
    //-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
    fun setView(maxLine: Int,
                gravity: Int,
                text: String,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                badgeList: List<BadgeData>?,
                lineMargin: Int = 0
    ) {

        this.mMaxLine = maxLine
        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mLineMargin = lineMargin

        setValidBadgeList(badgeList)
        setView()
    }

    fun setView(maxLine: Int,
                gravity: Int,
                text: SpannableString,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                badgeList: List<BadgeData>?,
                lineMargin: Int = 0
    ) {

        this.mMaxLine = maxLine
        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mLineMargin = lineMargin

        setValidBadgeList(badgeList)
        setView()
    }

    fun setView(maxLine: Int,
                gravity: Int,
                text: CharSequence,
                textSize: Int = 15,
                textColor: Int = R.color.hhyunctlib_black,
                badgeList: List<BadgeData>?,
                lineMargin: Int = 0
    ) {

        this.mMaxLine = maxLine
        this.mGravity = gravity
        this.mText = text
        this.mTextSize = textSize
        this.mTextColor = textColor
        this.mLineMargin = lineMargin

        setValidBadgeList(badgeList)
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


    fun setBadgeList(badgeList: List<BadgeData>?) {
        badgeList?.let {
            setValidBadgeList(badgeList)
            setView()
        }
    }

    private fun setValidBadgeList(badgeList: List<BadgeData>?) {
        if(badgeList != null) this.mBadgeList.clear()
        badgeList?.forEach {
            if(!it.labelText.isNullOrEmpty() || it.imageResId != null) {
                this.mBadgeList.add(it)
            }
        }
    }


    fun getText(): String = mText.toString()
    fun getBadgeText(index: Int): String = mBadgeList.getOrNull(index)?.labelText?.toString() ?: ""

}

