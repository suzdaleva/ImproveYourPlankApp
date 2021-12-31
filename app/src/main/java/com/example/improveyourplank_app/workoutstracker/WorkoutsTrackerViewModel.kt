package com.example.improveyourplank_app.workoutstracker

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.improveyourplank_app.database.WorkoutDatabaseDao
import kotlinx.coroutines.launch

class WorkoutsTrackerViewModel (
    dataSource: WorkoutDatabaseDao,
    application: Application
) : ViewModel(){
    val database = dataSource
    //val workouts = database.getAllWorkouts()

    private var _navigateToSomething = MutableLiveData<Boolean?>()
    val navigateToSomething : LiveData<Boolean?>
        get() = _navigateToSomething

    private var _navigateToTimer = MutableLiveData<Boolean?>()
    val navigateToTimer : LiveData<Boolean?>
        get() = _navigateToTimer

    fun onClose() {
        _navigateToTimer.value = true
    }

    fun doneNavigating() {
        _navigateToTimer.value = null
    }

    fun onWorkoutClicked(id: Long) {
        _navigateToSomething.value = true
    }

    private suspend fun deleteAll() {
        database.clear()
    }

    fun onDeleteAll() {
        viewModelScope.launch {
            deleteAll()

        }
    }

}