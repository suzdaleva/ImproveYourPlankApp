package com.example.improveyourplank_app.calendar.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.improveyourplank_app.MainActivity
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.database.WorkoutDatabase
import com.myapp.annoteapp.utils.*
import kotlinx.android.synthetic.main.day_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class DayAdapter(
    context: Context,
    private val calendarProperties: CalendarProperties,
    dates: MutableList<Date>,
    pageMonth: Int,
    val activity: MainActivity
) : ArrayAdapter<Date>(context, calendarProperties.itemLayoutResource, dates) {
    val application = activity.application
    val dataSource = WorkoutDatabase.getInstance(application).workoutDatabaseDao
    private val pageMonth = if (pageMonth < 0) 11 else pageMonth



    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val dayView = view
            ?: LayoutInflater.from(context).inflate(R.layout.day_view, parent, false)
        context.applicationContext
        val day = GregorianCalendar().apply {
            time = getItem(position)
        }
        val dayLabel = dayView.dayLabel

        CoroutineScope(Dispatchers.IO).launch {
            day.setMidnight()
            val tz: TimeZone = day.timeZone
            val zoneId: ZoneId = tz.toZoneId()
            val localDateTime = LocalDateTime.ofInstant(day.toInstant(), zoneId)
            val list = dataSource.getAllWorkouts()
            for(element in list) {
                if (day.get(Calendar.MONTH) == pageMonth && element.workoutDate.with(LocalTime.MIDNIGHT) == localDateTime) {
                    dayLabel.setBackgroundResource(R.drawable.checked_day)
                    dayLabel.setTextColor(context.parseColor(R.color.white))
                }
            }
            if (day.isToday && day.get(Calendar.MONTH) == pageMonth) {
                dayLabel.setBackgroundResource(R.drawable.today_circle)
                dayLabel.setTextColor(context.parseColor(R.color.black))
            }
            }
        dayLabel.typeface = ResourcesCompat.getFont(context, R.font.urbanist_bold)
        setLabelColors(dayLabel, day)
        dayLabel.text = day[Calendar.DAY_OF_MONTH].toString()
        return dayView
    }

    private fun setLabelColors(dayLabel: TextView, day: Calendar) {
        if (day.get(Calendar.MONTH) != pageMonth)
            dayLabel.setDayColors(calendarProperties.anotherMonthsDaysLabelsColor)
    }


    private fun Calendar.isCurrentMonthDay() = this[Calendar.MONTH] == pageMonth
            && !(calendarProperties.minimumDate != null
            && this.before(calendarProperties.minimumDate)
            || calendarProperties.maximumDate != null
            && this.after(calendarProperties.maximumDate))

}