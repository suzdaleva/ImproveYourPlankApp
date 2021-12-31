package com.myapp.annoteapp.utils

import java.util.*

fun Calendar.setMidnight() = this.apply {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

val midnightCalendar: Calendar
    get() = Calendar.getInstance().apply {
        this.setMidnight()
    }
val Calendar.isToday
    get() = this == midnightCalendar

fun Calendar.isEqual(calendar: Calendar) = this.setMidnight() == calendar.setMidnight()



