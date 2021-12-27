package com.hhyun.customlayout.overlaystatus.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.hhyun.customlayout.R
import com.hhyun.customlayout.overlaystatus.base.BaseOverlayStatusFragment

abstract class OverlayStatusDataBindingFragment<T: ViewDataBinding>(
    @LayoutRes private val contentLayoutId: Int,
    @ColorRes private val statusBarColor: Int = R.color.hhyuncllib_transparent,
    @ColorRes private val backgroundColorId: Int = R.color.hhyuncllib_white,
    isHideBackgroundGradient: Boolean? = false,
    isSecure: Boolean? = false
) : BaseOverlayStatusFragment(statusBarColor, backgroundColorId,
    isHideBackgroundGradient = isHideBackgroundGradient ?: false, isSecure = isSecure ?: false) {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        DataBindingUtil.inflate<T>(LayoutInflater.from(context), contentLayoutId, null, false).apply { binding = this }.root
        setBodyView(binding)
        super.onCreate(savedInstanceState)
    }
}