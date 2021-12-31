package com.example.improveyourplank_app.workoutstracker

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.convertLongToTimeString
import com.example.improveyourplank_app.database.Workout
import java.time.format.DateTimeFormatter

@BindingAdapter("workoutTimeFormatted")
fun TextView.setWorkoutTimeFormatted(item: Workout?) {
        item?.let { text = convertLongToTimeString(it.workoutTime, context.resources) }

}

@BindingAdapter("targetTimeFormatted")
fun TextView.setTargetTimeFormatted(item: Workout?) {
    item?.let { text = convertLongToTimeString(it.targetTime, context.resources) }
}

@BindingAdapter("workoutDateString")
fun TextView.setWorkoutDateString(item: Workout?) {
    item?.let { text = it.workoutDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) }
}



@BindingAdapter("feedbackString")
fun TextView.setFeedbackString(item: Workout?) {
    item.let {
        text = when (it?.workoutFeedback) {
            1 -> "Don't even ask!"
            2 -> "exhausted!"
            3 -> "SO-SO!"
            4 -> "pretty good!"
            5 -> "felling great!"
            else -> "not sure yet..."
        }
    }
}

@BindingAdapter("checkedStatus")
fun ImageView.setCheckedStatus(item: Workout?) {
    item?.let {
        if (item.workoutTime>=item.targetTime) setImageResource(R.drawable.checked_circle)
        else setImageResource(R.drawable.unchecked_circle)
    }
}