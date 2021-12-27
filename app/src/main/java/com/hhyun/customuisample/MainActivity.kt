package com.hhyun.customuisample

import android.content.Intent
import android.os.Bundle
import com.hhyun.customuisample.base.BaseActivity
import com.hhyun.customuisample.databinding.ActivityMainBinding
import com.hhyun.customuisample.textview.BadgeWrapTextViewTest
import com.hhyun.customuisample.textview.CharacterWrapTextViewTest

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onClick()
    }

    private fun onClick() {

        binding.buttonCharacterWrapTextView.setOnClickListener {
            startActivity(Intent(this@MainActivity, CharacterWrapTextViewTest::class.java))
        }

        binding.buttonBadgeWrapTextView.setOnClickListener {
            startActivity(Intent(this@MainActivity, BadgeWrapTextViewTest::class.java))
        }

        binding.buttonImageDrawableTextView.setOnClickListener {

        }

        binding.buttonSeparatorLinkedWrapTextView.setOnClickListener {

        }
    }

}