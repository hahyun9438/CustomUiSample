package com.hhyun.customtextviewlibrary.badge

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout

/**
 * 텍스트와 뱃지가 연달아 붙어 있는 커스텀 텍스트뷰
 *
 * <-------------텍스트------------->
 * <-------------텍스트------------->
 * <-----텍스트----><-뱃지-><-뱃지->
 * <-뱃지-><-뱃지-><-뱃지->
 */
class EndBadgeWrapTextView: BadgeWrapTextView {

    companion object {
        private const val TAG = "EbwTextView"
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override var badgeWrapType = BadgeWrapTextView.Companion.BadgeWrapType.END

    private var badgeList = arrayListOf<ArrayList<BadgeData>>()



    /**
     * 텍스트를 라인 당 넣을 수 있는 최대 글자수로 자른다.
     *
     * 예시)
     * 동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이보전하세.
     *
     * textList = [
     *  "동해물과 백두산이 마르고 닳도록 하느님이",
     *  "보우하사 우리나라만세 무궁화 삼천리 화려강산",
     *  "대한사람 대한으로 길이보전하세."
     * ]
     *
     * <동해물과 백두산이 마르고 닳도록 하느님이>
     * <보우하사 우리나라만세 무궁화 삼천리 화려강산>
     * <대한사람 대한으로 길이보...><-뱃지-><-뱃지->
     */
    override fun setView() {
        super.setView()

        if(mText.trim().isEmpty()) {
            removeAllViews()
            return
        }

        val tempTextView = getTempTextView(mText)
        addView(tempTextView)

        tempTextView.post {

            tempTextView.layout?.let {

                if(tempTextView.text.isNullOrEmpty()) return@post

                textList.clear()

                for (i in 0 until it.lineCount) {
                    val start = if(i == 0) 0 else it.getLineEnd(i - 1)
                    val end = it.getLineEnd(i)
                    val splitContext = tempTextView.text.subSequence(start, end)
                    textList.add(splitContext)
                }

                if(textList.isNotEmpty()) {
                    calculateTextList()
                }
            }

        }

    }


    private fun calculateTextList() {

        removeAllViews()

        this.badgeList.clear()

        val initBadgeList = getBadgeLineList()
        val badgeLineCount = initBadgeList.size


        /** 뱃지 없고 텍스트만 있는 경우 */
        if(badgeLineCount == 0) {
            setOnlyTextView()
            return
        }

        /**
         * 텍스트도 한줄, 뱃지도 한줄이고 한 라인에 텍스트와 뱃지를 모두 넣었을때 텍스트가 잘리지 않는 경우
         * <안녕하세요.><-뱃지-><-뱃지->
         */
        if(textList.size == 1
            && badgeLineCount == 1
            && !isNotEnoughWidthWithBadge(textList.first(), initBadgeList.first())) {

            this.badgeList.addAll(initBadgeList)

            addView(getTextWithBadgeView(textList.first(), badgeList.first()))

        } else {

            val hasTextWithBadgeLine = getTextMaxWidthExcludingBadge(initBadgeList.first()) > 0

            var tMaxLine = mMaxLine
            tMaxLine -= badgeLineCount
            if(hasTextWithBadgeLine) tMaxLine += 1

            if(tMaxLine < textList.size) this.badgeList.addAll(initBadgeList)
            else this.badgeList.addAll(getBadgeLineList((textList.lastOrNull())))

            setTextView(hasTextWithBadgeLine, tMaxLine)

        }

    }

    private fun setTextView(hasTextWithBadgeLine: Boolean, maxLine: Int) {

        val isTooBigMaxLine = textList.size < maxLine

        for(i in 0 until maxLine) {
            if(i > textList.size - 1) break

            val textItem = textList[i]

            when {
                isTooBigMaxLine && i == textList.size - 1 -> addView(getTextWithBadgeView(textItem, badgeList.first()))
                hasTextWithBadgeLine && i == maxLine - 1 -> {
                    val nextTextItem = textList.getOrNull(i + 1)
                    val fixTextItem = textItem.toString() + if(nextTextItem.isNullOrEmpty()) "" else nextTextItem.toString()
                    addView(getTextWithBadgeView(fixTextItem, badgeList.first()))
                }
                i == maxLine - 1 -> {
                    val nextTextItem = textList.getOrNull(i + 1)
                    val fixTextItem = textItem.toString() + if(nextTextItem.isNullOrEmpty()) "" else nextTextItem.toString()
                    val tv = getTextView(fixTextItem)
                    tv?.ellipsize = TextUtils.TruncateAt.END
                    addView(tv)
                }
                else -> addView(getTextView(textItem))
            }

            if(badgeList.size > 1) {
                addView(getLineView())
            }
        }

        val badgeLineIndex = if(hasTextWithBadgeLine || isTooBigMaxLine) 1 else 0
        for(i in badgeLineIndex until badgeList.size) {
            val badges = badgeList[i]
            addView(getBadgeLineView(badges))

            if(i < badgeList.size - 1) {
                addView(getLineView())
            }
        }

    }


    /** 뱃지 없고 텍스트만 있는 경우 */
    private fun setOnlyTextView() {
        var remainText = mText

        for(index in 0 until mMaxLine) {

            val textItem = textList.getOrNull(index) ?: ""
            val textView = getTextView(textItem)

            val isLast = if(textList.size < mMaxLine) index == textList.size - 1 else index == mMaxLine - 1

            textView?.let {
                if(isLast) {
                    it.text = remainText
                    it.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    it.ellipsize = TextUtils.TruncateAt.END
                } else {
                    remainText = remainText.removeRange(0, textItem.length)
                }

                addView(it)

                if(index < mMaxLine - 1 && !textList.getOrNull(index + 1).isNullOrEmpty()) {
                    addView(getLineView())
                }
            }

        }
    }

    private fun getTextWithBadgeView(textItem: CharSequence, badgeList: List<BadgeData>?): FlexboxLayout {

        val flexBoxLayout = FlexboxLayout(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            justifyContent = getFlexJustifyContent()
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.NOWRAP
            gravity = Gravity.CENTER_VERTICAL
            alignItems = AlignItems.CENTER
        }

        val textWidth = getTextWidth(textItem)
        val ableWidth = getTextMaxWidthExcludingBadge(badgeList).toInt()

        val textView = getCharacterWrapTextView(textItem, ableWidth)
        textView?.let {
            flexBoxLayout.addView(it)

            if(textWidth > ableWidth) {
                val tvLp = it.layoutParams as FlexboxLayout.LayoutParams
                tvLp.flexGrow = 1f
                it.layoutParams = tvLp
            }
        }

        if(!badgeList.isNullOrEmpty()) {
            flexBoxLayout.addView(getGapView(badgeList.firstOrNull()?.gapMargin ?: 0))

            val firstBadgeLineView = getBadgeLineView(badgeList)
            flexBoxLayout.addView(firstBadgeLineView)

            val badgeLp = firstBadgeLineView.layoutParams as FlexboxLayout.LayoutParams
            badgeLp.flexShrink = 0f
            firstBadgeLineView.layoutParams = badgeLp
        }

        return flexBoxLayout
    }



    /**
     * 각 줄에 들어갈 수 있는 뱃지를 그룹해서 리스트로 반환
     * 텍스트는 고려하지 않고 라인수만 체크하기 위함
     * [
     *  [뱃지-1, 뱃지-2],
     *  [뱃지-3, 뱃지-4, 뱃지-5, 뱃지-6],
     *  [뱃지-7, 뱃지-8, 뱃지-9, 뱃지-10]
     * ]
     */
    override fun getBadgeLineList(): ArrayList<ArrayList<BadgeData>> {

        val badgeLineList = arrayListOf<ArrayList<BadgeData>>()
        var oneLineBadgeWidth = 0f
        var oneLineBadgeList = arrayListOf<BadgeData>()

        mBadgeList.reversed().forEach { badge ->
            oneLineBadgeWidth += getBadgeWidth(badge)

            if(oneLineBadgeWidth > getTextContainerWidth()) {
                oneLineBadgeList.takeIf { it.isNotEmpty() }?.let {
                    it.reverse()
                    badgeLineList.add(it)
                }
                oneLineBadgeList = arrayListOf<BadgeData>()
                oneLineBadgeWidth = 0f
            }
            oneLineBadgeList.add(badge)
        }

        oneLineBadgeList.takeIf { it.isNotEmpty() }?.let {
            it.reverse()
            badgeLineList.add(it)
        }

        return arrayListOf<ArrayList<BadgeData>>().apply { addAll(badgeLineList.reversed()) }
    }


    /**
     * 뱃지가 1줄을 넘어가는 경우, 각 줄에 들어갈 수 있는 뱃지를 그룹해서 리스트로 반환
     * 같이 노출되어야 하는 텍스트의 너비가 고려된 리스트
     * [뱃지-1, 뱃지-2, 뱃지-3, 뱃지-4, 뱃지-5, 뱃지-6, 뱃지-7, 뱃지-8, 뱃지-9, 뱃지-10]
     *
     * ->
     *
     * [
     *  [뱃지-1],
     *  [뱃지-2, 뱃지-3, 뱃지-4, 뱃지-5],
     *  [뱃지-6, 뱃지-7, 뱃지-8, 뱃지-9],
     *  [뱃지-10]
     * ]
     */
    private fun getBadgeLineList(lastLineText: CharSequence?): ArrayList<ArrayList<BadgeData>> {

        val badgeLineList = arrayListOf<ArrayList<BadgeData>>()

        val textContainerWidth = getTextContainerWidth()
        var lineContainerWidth = textContainerWidth - getTextWidth(lastLineText)

        var oneLineBadgeWidth = 0f
        var oneLineBadgeList = arrayListOf<BadgeData>()

        mBadgeList.forEach { badge ->
            oneLineBadgeWidth += getBadgeWidth(badge)

            if(oneLineBadgeWidth > lineContainerWidth) {
                oneLineBadgeList.takeIf { it.isNotEmpty() }?.let { badgeLineList.add(it) }
                oneLineBadgeList = arrayListOf<BadgeData>()
                if(badgeLineList.size > 0) {
                    lineContainerWidth = textContainerWidth
                }
                oneLineBadgeWidth = 0f
            }
            oneLineBadgeList.add(badge)
        }

        oneLineBadgeList.takeIf { it.isNotEmpty() }?.let { badgeLineList.add(it) }

        return badgeLineList
    }


}