package com.example.improveyourplank_app.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity(tableName="workouts_table")
data class Workout(

  @PrimaryKey(autoGenerate = true)
  var workoutId : Long= 0L,

  @ColumnInfo(name = "workout_time")
  var workoutTime: Long = 0L,

  @ColumnInfo(name = "workout_date")
  var workoutDate: LocalDateTime = LocalDateTime.now(),

  @ColumnInfo(name = "target_time")
  var targetTime: Long = 0L,

  @ColumnInfo(name = "workout_feedback")
  var workoutFeedback: Int = 0

)