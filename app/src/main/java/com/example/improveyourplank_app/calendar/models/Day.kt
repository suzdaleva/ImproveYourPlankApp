package com.myapp.annoteapp.models

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import java.util.*

class Day (val calendar: Calendar){
    @ColorRes
    var labelColor: Int? = null

    @ColorRes
    var selectedLabelColor: Int? = null

    @DrawableRes
    var selectedBackgroundResource: Int? = null
    var selectedBackgroundDrawable: Drawable? = null

}