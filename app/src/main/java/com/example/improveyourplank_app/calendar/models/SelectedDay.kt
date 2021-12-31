package com.myapp.annoteapp.models

import android.view.View
import java.util.*

data class SelectedDay @JvmOverloads constructor(
    val calendar: Calendar,
    var view: View? = null
) {

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is SelectedDay -> calendar.isTheSameDay(other.calendar)
            is Calendar -> calendar.isTheSameDay(other)
            else -> super.equals(other)
        }
    }

    private fun Calendar.isTheSameDay(calendar: Calendar): Boolean =
        this.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) &&
                this.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                this.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
}