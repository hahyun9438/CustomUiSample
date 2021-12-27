package com.hhyun.customlayout.overlaystatus.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import com.hhyun.customlayout.overlaystatus.base.BaseOverlayStatusFragment

abstract class OverlayStatusFragment(@LayoutRes private val contentLayoutId: Int,
                                     @ColorRes statusBarColor: Int,
                                     @ColorRes backgroundColorId: Int,
                                     isHideBackgroundGradient: Boolean? = false,
                                     isSecure: Boolean? = false
): BaseOverlayStatusFragment(statusBarColor, backgroundColorId,
    isHideBackgroundGradient = isHideBackgroundGradient ?: false, isSecure = isSecure ?: false) {


    override fun onCreate(savedInstanceState: Bundle?) {
        setBodyView(LayoutInflater.from(context).inflate(contentLayoutId, null, false))
        super.onCreate(savedInstanceState)
    }


}
