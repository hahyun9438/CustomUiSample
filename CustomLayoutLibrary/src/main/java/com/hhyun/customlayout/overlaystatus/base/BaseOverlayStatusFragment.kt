package com.hhyun.customlayout.overlaystatus.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.hhyun.customlayout.R
import com.hhyun.customlayout.extension.hideKeyboard

abstract class BaseOverlayStatusFragment(
    @ColorRes private val statusBarColor: Int = R.color.hhyuncllib_transparent,
    @ColorRes private val backgroundColorId: Int = R.color.hhyuncllib_white,
    private var isHideBackgroundGradient: Boolean? = false,
    private val isSecure: Boolean = false): Fragment() {

    private var defaultFlag: Int = 0
    private var defaultSystemUiVisibility: Int = 0

    private var rootView: View? = null
    private var bodyView: View? = null
    private var llContainer: LinearLayout? = null
    private var popupBody: LinearLayout? = null


    private var overlayStatusFragmentListener: OverlayStatusFragmentListener? = null
    interface OverlayStatusFragmentListener {
        fun onFragmentOpen()
        fun onFragmentClose()
    }
    fun setOverlayStatusFragmentListener(listener: OverlayStatusFragmentListener) {
        this.overlayStatusFragmentListener = listener
    }

    private var onCloseButtonClickListener: OnClickCloseButtonListener? = null
    interface OnClickCloseButtonListener {
        fun onClick()
    }
    fun setOnClickCloseButtonListener(listener: OnClickCloseButtonListener) {
        this.onCloseButtonClickListener = listener
    }

    private var onGlobalLayoutListener: OnGlobalLayoutListener? = null
    interface OnGlobalLayoutListener {
        fun onGlobalLayout(popupHeaderHeight: Int?, popupBodyHeight: Int?)
    }
    fun setOnGlobalLayoutListener(listener: OnGlobalLayoutListener) {
        this.onGlobalLayoutListener = listener
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTranslucentStatus()
        super.onCreate(savedInstanceState)
        overlayStatusFragmentListener?.onFragmentOpen()
    }


    open fun setBodyView(@LayoutRes contentLayoutId: Int) {
        this.bodyView = LayoutInflater.from(context).inflate(contentLayoutId, null, false).apply {
            this.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    open fun setBodyView(view: View) {
        this.bodyView = view.apply {
            this.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    open fun setBodyView(binding: ViewDataBinding) {
        this.bodyView = binding.root.apply {
            this.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context: Context = ContextThemeWrapper(activity, R.style.HhyunClLib_AppTheme_OverlayStatus)
        val localInflater = inflater.cloneInContext(context)

        this.rootView = localInflater.inflate(R.layout.hhyuncllib_fragment_base_overlay_status, container, false)
        this.rootView?.isClickable = true

        this.llContainer = rootView?.findViewById<LinearLayout>(R.id.llContainer)
        setBackgroundGradientVisibility(isHideBackgroundGradient)

        this.popupBody = rootView?.findViewById<LinearLayout>(R.id.popupBody)

        popupBody?.removeAllViews()
        bodyView?.let { popupBody?.addView(it) }

        return rootView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        overlayStatusFragmentListener?.onFragmentClose()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setTranslucentStatus() {
        if(context == null) return
        llContainer?.setBackgroundColor(ContextCompat.getColor(context!!, backgroundColorId))
    }

    private fun resetStatus() {

        if(context == null) return

        llContainer?.setBackgroundColor(ContextCompat.getColor(context!!, R.color.hhyuncllib_transparent))

        activity?.window?.attributes?.flags = defaultFlag
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, 0)
        activity?.window?.decorView?.systemUiVisibility = defaultSystemUiVisibility

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity?.window?.statusBarColor = ContextCompat.getColor(context!!, statusBarColor)
        }

        activity?.findViewById<View>(android.R.id.content)?.requestLayout()

    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = activity?.window ?: return
        val winParams = win.attributes
        winParams.flags = if(on) winParams.flags or bits else winParams.flags and bits.inv()
        win.attributes = winParams
    }


    open fun closeFragment() {
        rootView?.hideKeyboard()
        try {
            activity?.supportFragmentManager?.popBackStackImmediate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun setBackgroundGradientVisibility(isHide: Boolean? = false) {
        this.isHideBackgroundGradient = isHide
        val backgroundResId = if(isHideBackgroundGradient == true) R.drawable.hhyuncllib_transparent
        else R.drawable.hhyuncllib_status_overlay_fragment_bg
        this.llContainer?.setBackgroundResource(backgroundResId)
    }

    fun setBackgroundGradientDrawable(@DrawableRes backgroundResId: Int) {
        this.isHideBackgroundGradient = false
        this.llContainer?.setBackgroundResource(backgroundResId)
    }

}