package com.example.improveyourplank_app.workoutstracker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.improveyourplank_app.database.WorkoutDatabaseDao

class WorkoutsTrackerViewModelFactory (
    private val dataSource: WorkoutDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutsTrackerViewModel::class.java)) {
            return WorkoutsTrackerViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
