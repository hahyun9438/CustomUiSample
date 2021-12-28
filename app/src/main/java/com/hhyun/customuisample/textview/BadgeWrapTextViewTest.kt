package com.hhyun.customuisample.textview

import android.os.Bundle
import android.util.Log
import com.hhyun.customtextviewlibrary.badge.BadgeData
import com.hhyun.customtextviewlibrary.badge.ImageBadge
import com.hhyun.customtextviewlibrary.util.Util
import com.hhyun.customuisample.R
import com.hhyun.customuisample.base.BaseActivity
import com.hhyun.customuisample.databinding.ActivityBadgeWrapTextViewTestBinding

class BadgeWrapTextViewTest: BaseActivity<ActivityBadgeWrapTextViewTestBinding>(R.layout.activity_badge_wrap_text_view_test) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        Log.e("hahTest", "deviceWidth = ${Util.getDeviceWidth(this)}")
//        Log.e("hahTest", "20dp = ${Util.dpToPx(this, 20)}")

        setBadgeWrapTextView()
    }

    private fun setBadgeWrapTextView() {

        val badges1 = arrayListOf<BadgeData>().apply {
            add(imageBadge(R.drawable.sample_badge_mermaid))
            add(textBadge("공유마당"))
        }

        val badges2 = arrayListOf<BadgeData>().apply {
//            add(textBadge("2021-12-27"))
//            add(textBadge("공유마당"))
//            add(textBadge("안데르센"))
//            add(textBadge("동화"))
//            add(imageBadge(R.drawable.sample_badge_seashell))
            add(imageBadge(R.drawable.sample_badge_mermaid))
            add(textBadge("2021-12-27"))
            add(textBadge("공유마당"))
            add(textBadge("안데르센"))
            add(textBadge("동화"))
            add(imageBadge(R.drawable.sample_badge_seashell))
            add(imageBadge(R.drawable.sample_badge_sea))
        }


        binding.sbwtv1.setBadgeList(badges1)
        binding.sbwtv2.setBadgeList(badges1)
        binding.sbwtv3.setBadgeList(badges1)
        binding.sbwtv4.setBadgeList(badges1)
        binding.sbwtv5.setBadgeList(badges1)
        binding.sbwtv6.setBadgeList(badges1)
        binding.sbwtv7.setBadgeList(badges2)
        binding.sbwtv8.setBadgeList(badges2)

        binding.ebwtv1.setBadgeList(badges1)
        binding.ebwtv2.setBadgeList(badges1)
        binding.ebwtv3.setBadgeList(badges1)
        binding.ebwtv4.setBadgeList(badges1)
        binding.ebwtv5.setBadgeList(badges1)
        binding.ebwtv6.setBadgeList(badges1)
        binding.ebwtv7.setBadgeList(badges2)
        binding.ebwtv8.setBadgeList(badges2)

    }

    private fun textBadge(label: String): BadgeData {
        return BadgeData(
            labelText = label,
            labelTextSize = 11,
            labelTextColor = R.color.sample_9da9d9,
            background = R.drawable.rect_trans_9da9d9_border_1_radius_6,
            width = null,
            height = 20,
            topPadding = 2f,
            bottomPadding = 2f,
            leftPadding = 4f,
            rightPadding = 4f,
            gapMargin = 6
        )
    }

    private fun imageBadge(resId: Int): BadgeData {
        return ImageBadge(
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






}