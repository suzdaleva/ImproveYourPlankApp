package com.example.improveyourplank_app.timer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.improveyourplank_app.database.WorkoutDatabaseDao
import com.example.improveyourplank_app.workoutstracker.WorkoutsTrackerViewModel

class TimerViewModelFactory (
    private val dataSource: WorkoutDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            return TimerViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}