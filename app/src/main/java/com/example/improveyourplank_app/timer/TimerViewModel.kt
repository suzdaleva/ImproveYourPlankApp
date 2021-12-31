package com.example.improveyourplank_app.timer

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.improveyourplank_app.database.Workout
import com.example.improveyourplank_app.database.WorkoutDatabaseDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TimerViewModel(
    dataSource: WorkoutDatabaseDao,
    application: Application
) : ViewModel() {

    val database = dataSource
    private val viewModelJob = Job()


    var timerRunning = false
    var isLaunched = false

    private var _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    var _targetTime = MutableLiveData<Long>()
    val targetTime: LiveData<Long>
        get() = _targetTime


    private val _navigateToWorkoutFeedback = MutableLiveData<Workout?>()
    val navigateToWorkoutFeedback: LiveData<Workout?>
        get() = _navigateToWorkoutFeedback

    //val workouts = database.getAllWorkouts()

    private val _isTargetTimeSet = MutableLiveData<Boolean?>()
    val isTargetTimeSet: LiveData<Boolean?>
        get() = _isTargetTimeSet

    fun doneNavigating() {
        _navigateToWorkoutFeedback.value = null
    }


    init {
        _currentTime.value = 0L
        _targetTime.value = 0L
    }


    private suspend fun insert(workout: Workout) {
        database.insert(workout)
    }

    private suspend fun update(workout: Workout) {
        database.update(workout)
    }


    fun onStop() {
        viewModelScope.launch {
            val newWorkout = Workout()
            newWorkout.workoutDate = LocalDateTime.now()
            newWorkout.workoutTime = currentTime.value!!
            newWorkout.targetTime = targetTime.value!!

            insert(newWorkout)

            Log.i(
                "I/Workout",
                "Ili vse le ${database.getThisWorkout()?.workoutId}   ${database.getThisWorkout()?.workoutTime}"
            )
            _navigateToWorkoutFeedback.value = database.getThisWorkout()
        }
    }

    fun onStartAndPause() {
        if (isLaunched) {
            if (timerRunning) timerRunning = false
            else {
                timerRunning = true
                viewModelScope.launch {
                    while (timerRunning) {
                        _currentTime.value = (currentTime.value)?.plus(1)
                        delay(1000)
                    }
                }
            }
        }
    }

    fun onLaunch() {
        timerRunning = true
        isLaunched = true
        viewModelScope.launch {
            while (timerRunning) {
                _currentTime.value = (currentTime.value)?.plus(1)
                delay(1000)
            }
        }
    }

    fun onSetTargetTime() {
        _isTargetTimeSet.value = true
    }

    fun doneSettingTime() {
        _isTargetTimeSet.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}