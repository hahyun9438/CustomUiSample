package com.hhyun.customtextviewlibrary.badge

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

/**
 * 뱃지와 텍스트가 연달아 붙어 있는 커스텀 텍스트뷰
 *
 * <-뱃지-><-뱃지-><-------텍스트------>
 * <-------------텍스트------------->
 * <---------텍스트------->
 *
 * <-뱃지-><-뱃지-><-뱃지-><-뱃지->
 * <-뱃지-><-뱃지-><-------텍스트------>
 * <-------------텍스트------------->
 * <---------텍스트------->
 */
class StartBadgeWrapTextView: BadgeWrapTextView {

    companion object {
        private const val TAG = "SbwTextView"
    }


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override var badgeWrapType = BadgeWrapTextView.Companion.BadgeWrapType.START

    private var badgeList = arrayListOf<ArrayList<BadgeData>>()


    override fun setView() {
        super.setView()

        badgeList = arrayListOf<ArrayList<BadgeData>>().apply { addAll(getBadgeLineList()) }
        badgeList.forEach { badges ->
            setView(badges)
        }
    }

    private fun setView(badgeList: ArrayList<BadgeData>) {

        if(mText.trim().isEmpty()) {
            removeAllViews()
            return
        }

        val tempTextView = getTempTextView(mText, getTextMaxWidthExcludingBadge(badgeList))
        addView(tempTextView)


        /**
         * 뱃지와 함께 들어가야 하는 첫번째 텍스트뷰에 담길 텍스트를 계산한다.
         * <-뱃지-><-뱃지-><-------텍스트------> <- 이 텍스트 뷰
         * <-------------텍스트------------->
         * <---------텍스트------->
         */

        tempTextView.post {

            tempTextView.layout?.let { ttv ->

                if(tempTextView.text.isEmpty()) return@post

                if(ttv.lineCount > 0) {
                    val firstLine = tempTextView.text.subSequence(0, ttv.getLineEnd(0))
                    val otherLine = mText.subSequence(firstLine.length, mText.length)

                    // 첫번째 텍스트뷰의 텍스트를 제외한 나머지 텍스트를 라인 당 넣을 수 있는 최대 글자수로 자른다.
                    calculateTextList(firstLine, otherLine)
                }
            }

        }

    }


    /**
     * @param firstLine 첫번째 텍스트뷰에 담길 텍스트
     * @param otherLine 첫번째 텍스트를 제외한 나머지 텍스트
     *
     * 예시)
     * 동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이보전하세.
     * firstLine = 동해물과 백두산이 마르고
     * otherLine = 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이보전하세.
     *
     * textList = [
     *  "동해물과 백두산이 마르고",
     *  "닳도록 하느님이 보우하사 우리나라만세 무궁화",
     *  "삼천리 화려강산 대한사람 대한으로 길이보전하세."
     * ]
     *
     * <-뱃지-><-뱃지-><동해물과 백두산이 마르고>
     * <닳도록 하느님이 보우하사 우리나라만세 무궁화>
     * <삼천리 화려강산 대한사람 대한으로 길이보...>
     */
    private fun calculateTextList(firstLine: CharSequence, otherLine: CharSequence) {

        val hasTextWithBadgeLine = getTextMaxWidthExcludingBadge(badgeList.last()) > 0
        val badgeLineCount = badgeList.size

        val tempTextView = getTempTextView(otherLine)
        addView(tempTextView)

        tempTextView.post {

            tempTextView.layout?.let { ttv ->

                val tempTextList = arrayListOf<CharSequence>()

                for (i in 0 until ttv.lineCount) {
                    val start = if(i == 0) 0 else ttv.getLineEnd(i - 1)
                    val end = ttv.getLineEnd(i)
                    val splitContext = tempTextView.text.subSequence(start, end)
                    tempTextList.add(splitContext)
                }

                textList.clear()
                textList.add(firstLine)

                val totalLine = if(hasTextWithBadgeLine) badgeLineCount + tempTextList.size
                else badgeLineCount + tempTextList.size + 1

                val maxLineCount = if(hasTextWithBadgeLine) mMaxLine - badgeLineCount
                else mMaxLine - badgeLineCount - 1

                if(totalLine <= mMaxLine) {
                    textList.addAll(tempTextList)

                } else if(maxLineCount > 0) {
                    textList.addAll(tempTextList.subList(0, maxLineCount - 1))
                    textList.add(tempTextList.subList(maxLineCount - 1, tempTextList.size).joinToString(""))
                }

                setTextView(hasTextWithBadgeLine)
            }

        }


    }


    private fun setTextView(hasTextWithBadgeLine: Boolean) {

        removeAllViews()

        badgeList.forEachIndexed { index, badges ->
            if(index < badgeList.size - 1) {
                addView(getBadgeLineView(badges))
                addView(getLineView())
            }

            // 뱃지와 텍스트가 한 라인에 같이 삽입되는 경우가 존재하지 않는 경우에만, 마지막 줄까지 넣는다.
            // <-뱃지-><-뱃지-><-뱃지-><-뱃지->
            // <동해물과 백두산이 마르고 닳도록>
            // <하느님이 보우하사 우리나라만세 무궁화>
            if(!hasTextWithBadgeLine && index == badgeList.size - 1) {
                addView(getBadgeLineView(badges))
                addView(getLineView())
            }
        }

        textList.forEachIndexed { index, textItem ->

            if(hasTextWithBadgeLine && index == 0) {
                // 첫줄에 뱃지랑 텍스트를 같이 넣는다.
                addView(getTextWithBadgeView(if(mMaxLine == 1) mText else textItem))

            } else {
                // 나머지 줄에는 텍스트를 차례로 넣는다.
                getTextView(textItem)?.let {
                    if(index == textList.size - 1) it.ellipsize = TextUtils.TruncateAt.END
                    addView(it)
                }
            }

            // 라인과 라인 사이에 라인뷰를 넣는다.
            if(mLineMargin > 0 && index < textList.size - 1 && !textList.getOrNull(index + 1).isNullOrEmpty()) {
                addView(getLineView())
            }
        }

    }

    private fun getTextWithBadgeView(textItem: CharSequence): LinearLayout {

        val linearLayout = LinearLayout(context).apply {
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.orientation = HORIZONTAL
            this.gravity = getParentGravity()
        }

        val textView = getTextView(textItem)?.apply {
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            if(mMaxLine == 1) this.ellipsize = TextUtils.TruncateAt.END
        }

        val badges = badgeList.lastOrNull()
        if(!badges.isNullOrEmpty()) {
            val badgeTextLineView = getBadgeLineView(badges)
            linearLayout.addView(badgeTextLineView)
        }

        textView?.let {
            linearLayout.addView(getGapView(badges?.lastOrNull()?.gapMargin ?: 0))
            linearLayout.addView(it)
        }

        return linearLayout
    }



    /**
     * 뱃지가 1줄을 넘어가는 경우, 각 줄에 들어갈 수 있는 뱃지를 그룹해서 리스트로 반환
     * [뱃지-1, 뱃지-2, 뱃지-3, 뱃지-4, 뱃지-5, 뱃지-6, 뱃지-7, 뱃지-8, 뱃지-9, 뱃지-10]
     *
     * ->
     *
     * [
     *  [뱃지-1, 뱃지-2, 뱃지-3, 뱃지-4],
     *  [뱃지-5, 뱃지-6, 뱃지-7, 뱃지-8],
     *  [뱃지-9, 뱃지-10]
     * ]
     */
    override fun getBadgeLineList(): ArrayList<ArrayList<BadgeData>> {

        val textContainerWidth = getTextContainerWidth()

        val badgeLineList = arrayListOf<ArrayList<BadgeData>>()
        var oneLineBadgeWidth = 0f
        var oneLineBadgeList = arrayListOf<BadgeData>()

        mBadgeList.forEachIndexed { index, badge ->

            oneLineBadgeWidth += getBadgeWidth(badge)

            if(oneLineBadgeWidth > textContainerWidth) {
                oneLineBadgeList.takeIf { it.isNotEmpty() }?.let { badgeLineList.add(it) }
                oneLineBadgeList = arrayListOf<BadgeData>()
                oneLineBadgeWidth = 0f
            }

            oneLineBadgeList.add(badge)

        }

        oneLineBadgeList.takeIf { it.isNotEmpty() }?.let { badgeLineList.add(it) }

        return badgeLineList
    }



}