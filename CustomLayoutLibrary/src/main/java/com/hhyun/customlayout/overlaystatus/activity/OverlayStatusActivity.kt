package com.hhyun.customlayout.overlaystatus.activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.hhyun.customlayout.R
import kotlinx.android.synthetic.main.hhyuncllib_activity_base_overlay_status.*

abstract class OverlayStatusActivity<T: ViewDataBinding>(
    @LayoutRes private val contentLayoutId: Int,
    private val isSecure: Boolean = false,
    private val isControlScreenBrightness: Boolean = false,
    private var isHideBackgroundGradient: Boolean? = false
): AppCompatActivity() {

    companion object {
        const val KEY_NO_ANIMATION = "KEY_NO_ANIMATION"
    }

    protected lateinit var binding: T
    private var originScreenBrightness: Float = -1f
    private var noAnimation = false

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

        val bodyView = DataBindingUtil.inflate<T>(LayoutInflater.from(this), contentLayoutId, null, false).apply { binding = this }.root

        // api26(8.0)에서 SDK 27이상으로할 때 Translucent/Floating 으로 만든 투명한 Activity들은 화면 회전고정을 하지 못하게 함
        try { requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT } catch (e: Exception) { }

        this.noAnimation = intent.getBooleanExtra(KEY_NO_ANIMATION, false)
        if (noAnimation) overridePendingTransition(R.anim.hhyuncllib_slide_inout_none, R.anim.hhyuncllib_slide_inout_none)
        else overridePendingTransition(R.anim.hhyuncllib_slide_in_up, R.anim.hhyuncllib_slide_inout_none)

        setContentView(R.layout.hhyuncllib_activity_base_overlay_status)
        setBackgroundGradientVisibility(isHideBackgroundGradient)
        window.setBackgroundDrawableResource(R.drawable.hhyuncllib_overlay_status_activity_window_bg)

        bodyView.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        popupBody.removeAllViews()
        popupBody.addView(bodyView)

    }


    override fun onResume() {
        super.onResume()
        setScreenBrightness()
    }

    override fun onPause() {
        resetScreenBrightness()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun finish() {
        super.finish()
        if (noAnimation) overridePendingTransition(R.anim.hhyuncllib_slide_inout_none, R.anim.hhyuncllib_slide_inout_none)
        else overridePendingTransition(R.anim.hhyuncllib_slide_inout_none, R.anim.hhyuncllib_slide_out_down)
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        overridePendingTransition(R.anim.mtlib_slide_inout_none, R.anim.mtlib_slide_out_down)
    }

    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            super.setRequestedOrientation(requestedOrientation)
        }
    }

    private fun setTranslucentStatus() {

        if (isSecure) { // 캡쳐방지
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setWindowFlag()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowFlag()
            window.statusBarColor = Color.TRANSPARENT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags: Int = window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.decorView.systemUiVisibility = flags
            setWindowFlag()
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return

        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        val winParams = window.attributes
        winParams.flags = winParams.flags and bits.inv()
        window.attributes = winParams
    }



    private fun setScreenBrightness() {
        originScreenBrightness = window.attributes?.screenBrightness ?: -1f
        setScreenBrightness(1f)
    }

    private fun resetScreenBrightness() {
        setScreenBrightness(originScreenBrightness)
    }

    private fun setScreenBrightness(brightness: Float) {
        if (!isControlScreenBrightness) return

        val winParams = window.attributes
        winParams?.screenBrightness = brightness
        window.attributes = winParams
    }




    fun setBackgroundGradientVisibility(isHide: Boolean? = false) {
        this.isHideBackgroundGradient = isHide
        val backgroundResId = if(isHideBackgroundGradient == true) R.drawable.hhyuncllib_transparent
        else R.drawable.hhyuncllib_status_overlay_activity_bg
        this.clPopup?.setBackgroundResource(backgroundResId)
    }

    fun setBackgroundGradientDrawable(@DrawableRes backgroundResId: Int) {
        this.isHideBackgroundGradient = false
        this.clPopup?.setBackgroundResource(backgroundResId)
    }

}