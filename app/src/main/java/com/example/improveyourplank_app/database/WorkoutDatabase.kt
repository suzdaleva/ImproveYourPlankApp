package com.example.improveyourplank_app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [Workout::class], version = 1, exportSchema = false)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract val workoutDatabaseDao: WorkoutDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null
        fun getInstance(context: Context): WorkoutDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        "workouts_history_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}