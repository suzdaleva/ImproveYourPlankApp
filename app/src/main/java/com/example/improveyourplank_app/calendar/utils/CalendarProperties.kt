package com.myapp.annoteapp.utils

import android.content.Context
import com.example.improveyourplank_app.R
import com.myapp.annoteapp.models.Day
import com.myapp.annoteapp.models.SelectedDay
import java.util.*

typealias OnPagePrepareListener = (Calendar) -> List<Day>
class CalendarProperties(private val context: Context) {
    var selectionColor: Int = 0
    var itemLayoutResource: Int = R.layout.day_view
    var selectionBackground: Int = 0
    var selectionLabelColor: Int = 0
    val firstPageCalendarDate: Calendar = midnightCalendar
    var firstDayOfWeek = firstPageCalendarDate.firstDayOfWeek
    var calendar: Calendar? = null
    var minimumDate: Calendar? = null
    var maximumDate: Calendar? = null
    var selectionDisabled: Boolean = false
    var calendarDayProperties: MutableList<Day> = mutableListOf()
    var onPagePrepareListener: OnPagePrepareListener? = null
    var anotherMonthsDaysLabelsColor: Int = context.parseColor(R.color.transparent)

    var selectedDays = mutableListOf<SelectedDay>()
        private set

    fun setSelectedDay(calendar: Calendar) = setSelectedDay(SelectedDay(calendar))

    fun setSelectedDay(selectedDay: SelectedDay) {
        selectedDays.clear()
        selectedDays.add(selectedDay)
    }
    fun findDayProperties(day: Calendar): Day? =
        calendarDayProperties.find { it.calendar.isEqual(day) }

    companion object {
        const val CALENDAR_SIZE = 2400
        const val FIRST_VISIBLE_PAGE = CALENDAR_SIZE/2
    }
}