package com.hhyun.customtextviewlibrary.badge

import com.hhyun.customtextviewlibrary.R

enum class BadgeType {
    TEXT, IMAGE, NONE
}


open class BadgeData(
    open var labelText: CharSequence = "",
    open var labelTextSize: Int = 11,
    open var labelTextColor: Int = R.color.hhyunctlib_grey_666,
    open var imageResId: Int = R.drawable.hhyunctlib_transparent,
    open var background: Int? = null,
    open var width: Int? = null,
    open var height: Int? = null,
    open var topPadding: Float = 0f,
    open var bottomPadding: Float = 0f,
    open var leftPadding: Float = 0f,
    open var rightPadding: Float = 0f,
    open var gapMargin: Int = 0
) {

    val badgeType: BadgeType
        get() = when {
            this is TextBadge || labelText.isNotEmpty() -> BadgeType.TEXT
            this is ImageBadge || imageResId != R.drawable.hhyunctlib_transparent -> BadgeType.IMAGE
            else -> BadgeType.NONE
        }

    val isValidBadge: Boolean get() = badgeType != BadgeType.NONE

    override fun toString(): String {
        return "BadgeData(labelText=$labelText, imageResId=$imageResId)"
    }
}

data class TextBadge(
    override var labelText: CharSequence,
    override var labelTextSize: Int = 11,
    override var labelTextColor: Int = R.color.hhyunctlib_grey_666,
    override var width: Int? = null,
    override var height: Int? = null,
    override var topPadding: Float = 0f,
    override var bottomPadding: Float = 0f,
    override var leftPadding: Float = 0f,
    override var rightPadding: Float = 0f,
    override var gapMargin: Int = 0

): BadgeData() {

    override fun toString(): String {
        return "TextBadge(labelText=$labelText)"
    }
}

data class ImageBadge(
    override var imageResId: Int,
    override var background: Int? = R.drawable.hhyunctlib_transparent,
    override var width: Int? = null,
    override var height: Int? = null,
    override var topPadding: Float = 0f,
    override var bottomPadding: Float = 0f,
    override var leftPadding: Float = 0f,
    override var rightPadding: Float = 0f,
    override var gapMargin: Int = 0

): BadgeData() {

    override fun toString(): String {
        return "ImageBadge(imageResId=$imageResId)"
    }
}