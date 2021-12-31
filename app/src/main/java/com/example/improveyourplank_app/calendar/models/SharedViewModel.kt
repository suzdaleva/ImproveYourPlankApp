package com.myapp.annoteapp.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myapp.annoteapp.utils.CalendarProperties

class SharedViewModel : ViewModel() {
    private val _currentPosition = MutableLiveData<Int>()
    val currentPosition: LiveData<Int> = _currentPosition
    fun resetPosition() {
        _currentPosition.value = 0
    }
    fun setCurrentPosition(position: Int) {
        _currentPosition.value = position - CalendarProperties.FIRST_VISIBLE_PAGE
    }
    init{
        resetPosition()
    }
}