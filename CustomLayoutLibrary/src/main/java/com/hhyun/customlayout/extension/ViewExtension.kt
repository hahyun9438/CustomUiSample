package com.hhyun.customlayout.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View?.setVisible(visible: Boolean) {
    this?.visibility = if(visible) View.VISIBLE else View.GONE
}

fun View?.showKeyboard() {
    try {
        this?.let {
            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            it.requestFocus()
            imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    } catch (e: Exception) {}
}

fun View?.hideKeyboard(): Boolean {
    try {
        this?.let {
            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            return imm?.hideSoftInputFromWindow(it.windowToken, 0) ?: false
        }
    } catch (ignored: RuntimeException) { }

    return false
}