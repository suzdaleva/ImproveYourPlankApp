package com.example.improveyourplank_app.workoutfeedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.improveyourplank_app.database.WorkoutDatabaseDao

class WorkoutFeedbackViewModelFactory (
    private val workoutKey: Long,
    private val dataSource: WorkoutDatabaseDao) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutFeedbackViewModel::class.java)) {
            return WorkoutFeedbackViewModel(workoutKey, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}