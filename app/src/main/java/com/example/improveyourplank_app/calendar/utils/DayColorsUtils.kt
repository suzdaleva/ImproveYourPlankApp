package com.myapp.annoteapp.utils

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.improveyourplank_app.R

fun TextView.setDayColors(
    textColor: Int,
    typeface: Typeface? = null,
    backgroundRes: Int = R.drawable.background_transparent
) {
    typeface?.let { setTypeface(typeface) }
    setTextColor(textColor)
    setBackgroundResource(backgroundRes)
}

fun Context.parseColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)