package com.example.improveyourplank_app.timepicker

import androidx.annotation.IntDef
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*


/**
 * An interface for different implementations of the UI components of TimePicker.
 *
 *
 * The UI components expose a ClockFace and an alternative input method.
 */
internal interface TimePickerControls {
    /** The 12h periods for a 12h time format  */
    @IntDef(Calendar.AM, Calendar.PM)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ClockPeriod

    /** Types of active selection for time picker  */
    @IntDef(Calendar.SECOND, Calendar.MINUTE)
    @Retention(RetentionPolicy.SOURCE)
    annotation class ActiveSelection

    /** Sets the time in millis *  */
    fun updateTime(@ClockPeriod period: Int, minute: Int, @IntRange(from = 0) second: Int)

    /** Set what we need to select. *  */
    fun setActiveSelection(@ActiveSelection selection: Int)

    /** Set the values in the clock face.  */
//    fun setValues(clockValues: Array<String?>, @StringRes contentDescription: Int)
//    fun setHandRotation(rotation: Float)
}