package com.example.improveyourplank_app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDateTime
import java.util.*


@Dao
interface WorkoutDatabaseDao {

    @Insert
    suspend fun insert(workout: Workout)

    @Update
    suspend fun update(workout:Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workouts_table WHERE workoutID= :key")
    suspend fun get(key: Long) :Workout

    @Query("SELECT EXISTS (SELECT 1 FROM workouts_table WHERE workout_date = :workoutDate)")
    suspend fun workoutWithDateExists(workoutDate: LocalDateTime): Boolean

    @Query("SELECT * FROM workouts_table ORDER BY workoutId DESC")
    suspend fun getAllWorkouts() : List<Workout>


    @Query("SELECT * FROM workouts_table ORDER BY workoutId DESC LIMIT 1")
    suspend fun getThisWorkout(): Workout?

    @Query("DELETE FROM workouts_table")
    suspend fun clear()

    @Query("SELECT * from workouts_table WHERE workoutId = :key")
    fun getWorkoutWithId(key: Long): LiveData<Workout>

}