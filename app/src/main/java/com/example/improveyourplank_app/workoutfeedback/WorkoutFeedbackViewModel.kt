package com.example.improveyourplank_app.workoutfeedback

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.improveyourplank_app.database.Workout
import com.example.improveyourplank_app.database.WorkoutDatabaseDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class WorkoutFeedbackViewModel (
    private val workoutKey: Long = 0L,
    dataSource: WorkoutDatabaseDao
) : ViewModel(){

    private val viewModelJob = Job()
    val database = dataSource
    private val workout: LiveData<Workout>

    fun getWorkout() = workout



    private var _navigateToTimer = MutableLiveData<Boolean?>()
    val navigateToTimer : LiveData<Boolean?>
    get() = _navigateToTimer

    private var _isSaved = MutableLiveData<Boolean?>()
    val isSaved : LiveData<Boolean?>
        get() = _isSaved

    private var _isDeleted = MutableLiveData<Boolean?>()
    val isDeleted : LiveData<Boolean?>
        get() = _isDeleted


    private var _navigateToWorkoutsTracker = MutableLiveData<Boolean?>()
    val navigateToWorkoutsTracker  : LiveData<Boolean?>
        get() = _navigateToWorkoutsTracker

    init {
        workout = database.getWorkoutWithId(workoutKey)
    }



    fun onSetWorkoutFeedback(feedback : Int) {
        viewModelScope.launch {
            val thisWorkout = database.get(workoutKey)
            thisWorkout.workoutFeedback = feedback
            thisWorkout.workoutDate = LocalDateTime.now()
            database.update(thisWorkout)
            workout.value?.workoutFeedback = feedback
        }
    }

    fun onNavigateToTimer() {
        _navigateToTimer.value = true
    }

    fun onDeleteWorkout() {
        viewModelScope.launch {
            database.deleteWorkout(workout.value!!)
            //_navigateToWorkoutsTracker.value = true
            _isDeleted.value = true
        }
    }

    fun doneNavigating() {
        _navigateToTimer.value = null
    }

    fun onSave(){
        _isSaved.value = isDeleted.value!=true
    }
    fun onRedo(){
        viewModelScope.launch {
            if(isDeleted.value == null)  {database.deleteWorkout(workout.value!!)}
            _navigateToTimer.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun onNavigateToWorkoutsTracker() {
        if (isSaved.value != true && isDeleted.value == null) onDeleteWorkout()
        _navigateToWorkoutsTracker.value = true
    }

    fun doneNavigatingToWorkoutsTracker() {
        _navigateToWorkoutsTracker.value = null
    }
}