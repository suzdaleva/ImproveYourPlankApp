package com.myapp.annoteapp.models

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.myapp.annoteapp.utils.setMidnight
import java.util.*

data class EventDay(val day: Calendar) {
    private var imageDrawable: Int = 0
    private var labelColor: Int = 0

    init {
        day.setMidnight()
    }

    constructor(
        day: Calendar,
        @ColorInt labelColor: Int,
        @DrawableRes drawableRes: Int
    ) : this(day) {
        this.labelColor = labelColor
        this.imageDrawable = drawableRes
    }
}