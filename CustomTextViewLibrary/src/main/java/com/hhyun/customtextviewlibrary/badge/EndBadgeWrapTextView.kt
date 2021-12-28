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

        if(badgeLineCount == 0) { // 뱃지가 없고 텍스트만 있는 경우

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

            return
        }


        /**
         * 텍스트도 한줄, 뱃지도 한줄이고 한 라인에 텍스트와 뱃지를 모두 넣었을때 텍스트가 잘리지 않는 경우
         * <안녕하세요.><-뱃지-><-뱃지->
         */
        if(textList.size == 1
            && badgeLineCount == 1
            && !isNotEnoughWidthWithBadge(textList.firstOrNull(), initBadgeList.firstOrNull())) {

            this.badgeList.addAll(initBadgeList)

            val textView = getTextView(textList.firstOrNull())
            setTextWithBadgeView(textView, badgeList.firstOrNull())

        } else {

            var tMaxLine = mMaxLine

            if(badgeLineCount > 1) {
                tMaxLine -= (badgeLineCount - 1)

                this.badgeList.addAll(
                    getBadgeLineList(
                        if(tMaxLine < textList.size) textList.getOrNull(tMaxLine) else textList.lastOrNull())
                )

            } else {
                this.badgeList.addAll(initBadgeList)
            }

            setTextView()

        }

    }

    override fun setTextView() {

        val addViewList = arrayListOf<View>()   // 임시 뷰 리스트 : 뷰를 거꾸로 담았다가 최종으로 rootView에 정상 순서로 add한다.

        if(badgeList.size == 1) {

            for(i in textList.size - 1 downTo 0) {
                getTextView(i, mMaxLine, badgeList[0])?.let { addViewList.add(it) }
            }

        } else {

            var justBadgeLineCount = 0  // 뱃지로만 구성된 라인 수

            for(index in badgeList.size - 1 downTo 0) {

                val badges = badgeList[index]

                if(index > 0) {
                    addViewList.add(getBadgeLineView(badges))   // 뱃지로만 구성된 뷰를 넣는다.
                    justBadgeLineCount++

                } else {

                    if(textList.size + justBadgeLineCount <= mMaxLine) {

                        // 그려야하는 모든 뷰를 그렸을때 최대 라인수를 넘지 않는 경우
                        // -> 모든 텍스트뷰를 넣는다.

                        for(i in textList.size - 1 downTo 0) {
                            if(i == textList.size - 1) {
                                getTextView(i, i + 1, badges)?.let { addViewList.add(it) }

                            } else {
                                getTextView(i, mMaxLine, badges)?.let { addViewList.add(it) }
                            }
                        }

                    } else {

                        // 그려야하는 모든 뷰를 그렸을때 최대 라인수를 넘는 경우
                        // -> 최대 라인까지만 뷰를 그려서 넣는다.

                        val tMaxLine = mMaxLine - justBadgeLineCount

                        if(mMaxLine == 1 && tMaxLine <= 0) {
                            // 최대 라인수가 1줄인데 뱃지로만 1줄이 넘어가는 경우에는 첫줄에 텍스트뷰를 무조건 넣는다.
                            getTextView(0, 1, badges)?.let { addViewList.add(it) }

                        } else {
                            for(i in tMaxLine - 1 downTo 0) {
                                getTextView(i, tMaxLine, badges)?.let { addViewList.add(it) }
                            }
                        }

                    }

                }

            }

        }




        /**
         * addViewList =
         * [
         *  뱃지라인뷰(<-뱃지-><-뱃지-><-뱃지-><-뱃지-><-뱃지->),
         *  라인뷰(-------------------),
         *  뱃지라인뷰(<-뱃지-><-뱃지-><-뱃지-><-뱃지-><-뱃지->),
         *  라인뷰(-------------------),
         *  텍스트&뱃지뷰(<-----텍스트------><-뱃지-><-뱃지->),
         *  라인뷰(-------------------),
         *  텍스트뷰(<--------------텍스트--------------->),
         *  라인뷰(-------------------),
         *  텍스트뷰(<--------------텍스트--------------->),
         * ]
         *
         * -> rootView에 add
         *
         * <--------------텍스트------------->
         * --------라인-----------
         * <--------------텍스트------------->
         * --------라인-----------
         * <-----텍스트------><-뱃지-><-뱃지->
         * --------라인-----------
         * <-뱃지-><-뱃지-><-뱃지-><-뱃지-><-뱃지->
         * --------라인-----------
         * <-뱃지-><-뱃지-><-뱃지-><-뱃지-><-뱃지->
         */
        addViewList.reversed().forEachIndexed { index, view ->
            addView(view)
            if(index < addViewList.size - 1) {
                addView(getLineView())
            }
        }

    }

    private fun getTextView(index: Int, maxLine: Int, badges: List<BadgeData>): View? {

        val textItem = textList.getOrNull(index) ?: ""

        return if(index == maxLine - 1) {
            getTextWithBadgeView(textItem, badges)

        } else {
            getTextView(textItem)
        }

    }





    private fun setTextWithBadgeView(textView: TextView?, badgeList: List<BadgeData>?) {

        /**
         * 텍스트에 말줄임 처리를 해야하는 경우에는 LinearLayout에 담는다.
         *
         * 동해물과 백두산이 마르고...<-뱃지-><-뱃지->
         */

        val parent = LinearLayout(context).apply {
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.orientation = HORIZONTAL
            this.gravity = getParentGravity()
        }
        addView(parent)

        textView?.let {
            it.ellipsize = TextUtils.TruncateAt.END
            parent.addView(it)
        }

        if(!badgeList.isNullOrEmpty()) {

            val badge = badgeList.firstOrNull()
            val badgeLineTextView = getBadgeLineView(badgeList)

            parent.addView(getGapView(badge?.gapMargin ?: 0))
            parent.addView(badgeLineTextView)
        }
    }

    private fun getTextWithBadgeView(textItem: CharSequence, badgeList: List<BadgeData>?): FlexboxLayout {

        /**
         * 텍스트가 짧아 말줄임 처리 없이 wrap으로 넣어야 하는 경우에는 FlexboxLayout에 담는다.
         *
         * 안녕하세요.<-뱃지-><-뱃지->
         */

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