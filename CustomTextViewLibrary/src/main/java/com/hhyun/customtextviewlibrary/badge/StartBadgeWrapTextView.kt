package com.hhyun.customtextviewlibrary.badge

import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hhyun.customtextviewlibrary.R

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

        // 첫줄에 그려야하는 뱃지를 다 넣어도 텍스트를 넣을 공간이 있는 경우
        // <-뱃지-><-뱃지-><-------텍스트------>
        // <-------------텍스트------------->
        if(getTextMaxWidthExcludingBadge(mBadgeList) > 0) {
            badgeList = arrayListOf<ArrayList<BadgeData>>().apply { add(mBadgeList) }
            setView(mBadgeList)

        } else {

            // 1개의 줄 이상이 모두 뱃지로 채워져야 하는 경우
            // <-뱃지-><-뱃지-><-뱃지-><-뱃지->
            // <-뱃지-><-뱃지-><-------텍스트------>

            badgeList = arrayListOf<ArrayList<BadgeData>>().apply { addAll(getBadgeLineList()) }
            badgeList.forEachIndexed { index, badges ->

                if(index < badgeList.size - 1) {
                    mMaxLine--

                } else {
                    setView(badges)
                }

            }

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

                val tempTextList = arrayListOf<CharSequence>()

                for (i in 0 until ttv.lineCount) {
                    val start = if(i == 0) 0 else ttv.getLineEnd(i - 1)
                    val end = ttv.getLineEnd(i)
                    val splitContext = tempTextView.text.subSequence(start, end)
                    tempTextList.add(splitContext)
                }

                if(tempTextList.isNotEmpty()) {

                    val firstLine = tempTextList.first()

                    // 첫번째 텍스트뷰의 텍스트를 제외한 나머지 텍스트를 라인 당 넣을 수 있는 최대 글자수로 자른다.
                    calculateTextList(firstLine, mText.subSequence(firstLine.length, mText.length))

                }
            }

        }

    }


    /**
     * @param firstLine 첫번째 텍스트뷰에 담길 텍스트
     * @param cText 첫번째 텍스트를 제외한 나머지 텍스트
     *
     * 예시)
     * 동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이보전하세.
     * firstLine = 동해물과 백두산이 마르고
     * cText = 닳도록 하느님이 보우하사 우리나라만세 무궁화 삼천리 화려강산 대한사람 대한으로 길이보전하세.
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
    private fun calculateTextList(firstLine: CharSequence, cText: CharSequence) {

        val tempTextView = getTempTextView(cText)
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

                try {

                    val lastIndex = mMaxLine - 2
                    var i = 0
                    while (i < lastIndex) {
                        tempTextList.getOrNull(i)?.let { textList.add(it) }
                        i++
                    }

                    if(i > 0) {
                        for(j in i - 1 downTo 0) {
                            tempTextList.getOrNull(j)?.let { tempTextList.remove(it) }
                        }
                    }

                    textList.add(tempTextList.joinToString(""))


                } catch (e: Exception) {
                    e.printStackTrace()
                    mMaxLine = 2
                    textList.add(tempTextList.joinToString(""))
                }

                setTextView()
            }

        }


    }


    override fun setTextView() {

        removeAllViews()

        // 뱃지가 1줄 이상이면, 마지막 줄 제외하고 차례로 넣는다.
        if(badgeList.size > 1) {
            badgeList.forEachIndexed { index, badges ->
                if(index < badgeList.size - 1) {
                    addView(getBadgeLineView(badges))
                    addView(getLineView())
                }
            }
        }


        textList.forEachIndexed { index, textItem ->

            when(index) {

                // 첫줄에 뱃지랑 텍스트를 같이 넣는다.
                0 -> setTextWithBadgeView(if(mMaxLine == 1) mText else textItem)

                // 나머지 줄에는 텍스트를 차례로 넣는다.
                in 1 until mMaxLine -> {
                    getTextView(textItem)?.let {
                        if(index == mMaxLine - 1) {
                            it.ellipsize = TextUtils.TruncateAt.END
                        }
                        addView(it)
                    }
                }

            }

            // 라인과 라인 사이에 라인뷰를 넣는다.
            if(mLineMargin > 0 && index < mMaxLine - 1 && !textList.getOrNull(index + 1).isNullOrEmpty()) {
                addView(getLineView())
            }
        }

    }

    private fun setTextWithBadgeView(textItem: CharSequence) {

        val parent = LinearLayout(context).apply {
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            this.orientation = HORIZONTAL
            this.gravity = getParentGravity()
//            this.gravity = Gravity.CENTER_VERTICAL
        }
        addView(parent)

        val textView = getTextView(textItem)?.apply {
            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            this.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            if(mMaxLine == 1) this.ellipsize = TextUtils.TruncateAt.END
        }

        val badges = badgeList.lastOrNull()
        if(!badges.isNullOrEmpty()) {
            val badge = badges.lastOrNull()
            val badgeTextLineView = getBadgeLineView(badges)
            parent.addView(badgeTextLineView)
        }

        textView?.let {
            parent.addView(getGapView(badges?.lastOrNull()?.gapMargin ?: 0))
            parent.addView(it)
        }

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

        mBadgeList.forEach { badge ->

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