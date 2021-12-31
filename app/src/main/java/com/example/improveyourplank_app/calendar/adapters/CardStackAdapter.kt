package com.example.improveyourplank_app.calendar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.improveyourplank_app.MainActivity
import com.example.improveyourplank_app.R
import com.myapp.annoteapp.extensions.CalendarGridView
import com.myapp.annoteapp.utils.CalendarProperties
import com.myapp.annoteapp.utils.CalendarProperties.Companion.CALENDAR_SIZE
import com.myapp.annoteapp.utils.CalendarProperties.Companion.FIRST_VISIBLE_PAGE
import com.myapp.annoteapp.utils.setMidnight
import java.util.*

class CardStackAdapter(
    private val context: Context,
    val calendarProperties: CalendarProperties,
    val activity: MainActivity
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    private var pageMonth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_calendar, parent, false)
        view.elevation = 20f
        view.translationZ = 20f
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val days = mutableListOf<Date>()

        val calendar = (calendarProperties.firstPageCalendarDate.clone() as Calendar).apply {

            add(Calendar.MONTH, position - FIRST_VISIBLE_PAGE)


            set(Calendar.DAY_OF_MONTH, 1)
        }
        holder.year.text = (calendar.get(Calendar.YEAR)).toString()
        holder.month.text = months[calendar.get(Calendar.MONTH)]
        getPageDaysProperties(calendar)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val firstDayOfWeek = calendarProperties.firstDayOfWeek
        val monthBeginningCell =
            (if (dayOfWeek < firstDayOfWeek) 7 else 0) + dayOfWeek - firstDayOfWeek
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (days.size < 42) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        pageMonth = calendar.get(Calendar.MONTH) - 1
        val calendarDayAdapter = DayAdapter(context, calendarProperties, days, pageMonth, activity)

        holder.itemView.alpha = 0.0f

        holder.calendarGridView.adapter = calendarDayAdapter
    }


    override fun getItemCount(): Int {
        return CALENDAR_SIZE
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val month: TextView = view.findViewById(R.id.month)
        val year: TextView = view.findViewById(R.id.year)
        var calendarGridView: CalendarGridView = view.findViewById(R.id.calendarGridView)

    }

    val months = listOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    private fun getPageDaysProperties(calendar: Calendar) {
        val pageCalendarDays = calendarProperties.onPagePrepareListener?.invoke(calendar)
        if (pageCalendarDays != null) {
            val diff = pageCalendarDays.minus(calendarProperties.calendarDayProperties).distinct()
            calendarProperties.calendarDayProperties.addAll(diff)
        }
    }


}