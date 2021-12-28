package com.hhyun.customtextviewlibrary.util

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import java.util.*

object FontUtil {

    enum class FontType(val otfName: String, val code: Int) {
        BOLD("noto_bold", 0),
        MEDIUM("noto_medium", 1),
        REGULAR("noto_regular", 2),
        LIGHT("noto_light", 3),
    }

    private val cache = hashMapOf<String, Typeface>()
    private val fontCache = Hashtable<String, Typeface?>()

    fun getTypeface(context: Context, fontType: String): Typeface? {
        if (!cache.containsKey(fontType)) {
            try {
                cache[fontType] = Typeface.createFromAsset(context.assets, fontType)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return cache[fontType]
    }


    /**
     * @param name res/font/ 폴더에 있는 font otf 파일명
     */
    operator fun get(context: Context, name: String): Typeface? {
        var tf = fontCache[name]
        if (tf == null) {
            tf = try { ResourcesCompat.getFont(context, getFontResId(context, name)) } catch (e: Exception) { return null }
            fontCache[name] = tf
        }
        return tf
    }

    private fun getFontResId(context: Context, string: String?): Int {
        return context.resources.getIdentifier(string, "font", context.packageName)
    }


    fun getFontTypeface(context: Context, fontType: String): Typeface? {
        return get(context, fontType)
    }


    /**
     * Android 11 부터 bold 가 제대로 적용 안되는 버그가 있어 xml을 버전으로 분기하여 처리한다.
     */
    fun getBold(context: Context): Typeface? {
        return get(context, FontType.BOLD.otfName)
    }

    fun getMedium(context: Context): Typeface? {
        return get(context, FontType.MEDIUM.otfName)
    }

    fun getRegular(context: Context): Typeface? {
        return get(context, FontType.REGULAR.otfName)
    }

    fun getLight(context: Context): Typeface? {
        return get(context, FontType.LIGHT.otfName)
    }


    fun getFontByCode(context: Context, code: Int): Typeface? {
        return when(code) {
            FontType.BOLD.code -> getBold(context)
            FontType.MEDIUM.code -> getMedium(context)
            FontType.LIGHT.code -> getLight(context)
            else -> getRegular(context)
        }
    }

}