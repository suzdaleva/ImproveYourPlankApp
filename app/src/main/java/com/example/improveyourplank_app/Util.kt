package com.example.improveyourplank_app

import android.annotation.SuppressLint
import android.content.res.Resources
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun convertLongToTimeString(time: Long,  resources: Resources): String {
    return SimpleDateFormat("mm:ss")
        .format(time*1000).toString()
}

