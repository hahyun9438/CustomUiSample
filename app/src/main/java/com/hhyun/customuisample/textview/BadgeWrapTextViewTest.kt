package com.hhyun.customuisample.textview

import android.os.Bundle
import android.util.Log
import com.hhyun.customtextviewlibrary.badge.BadgeData
import com.hhyun.customtextviewlibrary.util.Util
import com.hhyun.customuisample.R
import com.hhyun.customuisample.base.BaseActivity
import com.hhyun.customuisample.databinding.ActivityBadgeWrapTextViewTestBinding

class BadgeWrapTextViewTest: BaseActivity<ActivityBadgeWrapTextViewTestBinding>(R.layout.activity_badge_wrap_text_view_test) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("hahTest", "deviceWidth = ${Util.getDeviceWidth(this)}")
        Log.e("hahTest", "20dp = ${Util.dpToPx(this, 20)}")

        setBadgeWrapTextView()
    }

    private fun setBadgeWrapTextView() {

        val badges1 = arrayListOf<BadgeData>().apply {
            add(imageBadge(R.drawable.sample_badge_mermaid))
            add(textBadge("공유마당"))
        }
        binding.sbwtv1.setBadgeList(badges1)
        binding.sbwtv2.setBadgeList(badges1)
        binding.sbwtv3.setBadgeList(badges1)


        val badges2 = arrayListOf<BadgeData>().apply {
            add(textBadge("인어공주"))
            add(textBadge("공유마당"))
            add(textBadge("2021-12-27"))
            add(imageBadge(R.drawable.sample_badge_seashell))
        }
        binding.sbwtv4.setBadgeList(badges2)

        binding.ebwtv1.setBadgeList(badges1)
        binding.ebwtv2.setBadgeList(badges1)
        binding.ebwtv3.setBadgeList(badges1)
        binding.ebwtv4.setBadgeList(badges2)

    }

    private fun imageBadge(resId: Int): BadgeData {
        return BadgeData(
            labelText = "",
            labelTextSize = null,
            labelTextColor = null,
            imageResId = resId,
            background = null,
            width = null,
            height = null,
            topPadding = 0f,
            bottomPadding = 0f,
            leftPadding = 0f,
            rightPadding = 0f,
            gapMargin = 6
        )
    }

    private fun textBadge(label: String): BadgeData {
        return BadgeData(
            labelText = label,
            labelTextSize = 11,
            labelTextColor = R.color.sample_8f65b0,
            imageResId = null,
            background = R.drawable.rect_trans_8f65b0_border_1_radius_6,
            width = null,
            height = 20,
            topPadding = 1f,
            bottomPadding = 2f,
            leftPadding = 4f,
            rightPadding = 4f,
            gapMargin = 6
        )
    }




}