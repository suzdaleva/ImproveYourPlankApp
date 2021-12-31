package com.example.improveyourplank_app.timepicker

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import android.widget.Checkable
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import com.example.improveyourplank_app.R
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import java.util.*
//import com.example.improveyourplank_app.timepicker.ClockHandView.OnRotateListener
//import com.example.improveyourplank_app.timepicker.ClockHandView.OnActionUpListener


/**
 * The main view to display a time picker.
 *
 *
 *  A time picker prompts the user to choose the time of day.
 *
 */
internal class TimePickerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(context!!, attrs, defStyleAttr), TimePickerControls {
//    internal interface OnPeriodChangeListener {
//        fun onPeriodChange(@TimePickerControls.ClockPeriod period: Int)
//    }

    internal interface OnSelectionChange {
        fun onSelectionChanged(@TimePickerControls.ActiveSelection selection: Int)
    }

    internal interface OnDoubleTapListener {
        fun onDoubleTap()
    }

    private val secondView: Chip
    private val minuteView: Chip
//    private val clockHandView: ClockHandView
    //private val clockFace: ClockFaceView
    //private val toggle: MaterialButtonToggleGroup
    private val selectionListener =
        OnClickListener { v ->
            if (onSelectionChangeListener != null) {
                onSelectionChangeListener!!.onSelectionChanged(v.getTag(R.id.selection_type) as Int)
            }
        }
    //private var onPeriodChangeListener: OnPeriodChangeListener? = null
    private var onSelectionChangeListener: OnSelectionChange? = null
    private var onDoubleTapListener: OnDoubleTapListener? = null
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDoubleTap() {
        val gestureDetector = GestureDetector(
            context,
            object : SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    val listener = onDoubleTapListener
                    if (listener != null) {
                        listener.onDoubleTap()
                        return true
                    }
                    return false
                }
            })
        val onTouchListener = OnTouchListener { v, event ->
            if ((v as Checkable).isChecked) {
                gestureDetector.onTouchEvent(event)
            } else false
        }
        secondView.setOnTouchListener(onTouchListener)
        minuteView.setOnTouchListener(onTouchListener)
    }

    fun setMinuteHourDelegate(clickActionDelegate: AccessibilityDelegateCompat?) {
        ViewCompat.setAccessibilityDelegate(minuteView, clickActionDelegate)
    }

    fun setHourClickDelegate(clickActionDelegate: AccessibilityDelegateCompat?) {
        ViewCompat.setAccessibilityDelegate(secondView, clickActionDelegate)
    }

    private fun setUpDisplay() {
        secondView.setTag(R.id.selection_type, Calendar.SECOND)
        minuteView.setTag(R.id.selection_type, Calendar.MINUTE)
        secondView.setOnClickListener(selectionListener)
        minuteView.setOnClickListener(selectionListener)
    }

//    override fun setValues(values: Array<String?>, @StringRes contentDescription: Int) {
//        clockFace.setValues(values, contentDescription)
//    }
//
//    override fun setHandRotation(rotation: Float) {
//        clockHandView.setHandRotation(rotation)
//    }
//
//    fun setHandRotation(rotation: Float, animate: Boolean) {
//        clockHandView.setHandRotation(rotation, animate)
//    }
//
//    fun setAnimateOnTouchUp(animating: Boolean) {
//        clockHandView.setAnimateOnTouchUp(animating)
//    }

    @SuppressLint("DefaultLocale")
    override fun updateTime(@TimePickerControls.ClockPeriod period: Int, minute: Int, second: Int) {
//        val checkedId =
//            if (period == Calendar.PM) R.id.material_clock_period_pm_button else R.id.material_clock_period_am_button
//        toggle.check(checkedId)
        val current = resources.configuration.locale
        val secondFormatted = String.format(current, CustomTimeModel.ZERO_LEADING_NUMBER_FORMAT, second)
        val minuteFormatted = String.format(current, CustomTimeModel.ZERO_LEADING_NUMBER_FORMAT, minute)
        secondView.text = secondFormatted
        minuteView.text = minuteFormatted
    }

    override fun setActiveSelection(@TimePickerControls.ActiveSelection selection: Int) {
        secondView.isChecked = selection == Calendar.SECOND
        minuteView.isChecked = selection == Calendar.MINUTE
    }

//    fun addOnRotateListener(onRotateListener: OnRotateListener?) {
//        clockHandView.addOnRotateListener(onRotateListener!!)
//    }
//
//    fun setOnActionUpListener(onActionUpListener: OnActionUpListener?) {
//        clockHandView.setOnActionUpListener(onActionUpListener)
//    }

//    fun setOnPeriodChangeListener(onPeriodChangeListener: OnPeriodChangeListener?) {
//        this.onPeriodChangeListener = onPeriodChangeListener
//    }

    fun setOnSelectionChangeListener(
        onSelectionChangeListener: OnSelectionChange?
    ) {
        this.onSelectionChangeListener = onSelectionChangeListener
    }

    fun setOnDoubleTapListener(listener: OnDoubleTapListener?) {
        onDoubleTapListener = listener
    }

//    fun showToggle() {
//        toggle.visibility = VISIBLE
//    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView === this && visibility == VISIBLE) {
            //updateToggleConstraints()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //updateToggleConstraints()
    }

//    private fun updateToggleConstraints() {
//        if (toggle.visibility == VISIBLE) {
//            // The clock display would normally be centered, clear the constraint on one side to make
//            // room for the toggle
//            val constraintSet = ConstraintSet()
//            constraintSet.clone(this)
//            val isLtr = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR
//            val sideToClear = if (isLtr) ConstraintSet.RIGHT else ConstraintSet.LEFT
//            constraintSet.clear(R.id.material_clock_display, sideToClear)
//            constraintSet.applyTo(this)
//        }
//    }

    init {
        LayoutInflater.from(context).inflate(R.layout.material_timepicker, this)
        ///clockFace = findViewById(R.id.material_clock_face)
        //toggle = findViewById(R.id.material_clock_period_toggle)
//        toggle.addOnButtonCheckedListener { group, checkedId, isChecked ->
////            val period =
////                if (checkedId == R.id.material_clock_period_pm_button) Calendar.PM else Calendar.AM
////            if (onPeriodChangeListener != null && isChecked) {
////                onPeriodChangeListener!!.onPeriodChange(period)
////            }
//        }
        secondView = findViewById(R.id.material_minute_tv)
        minuteView = findViewById(R.id.material_hour_tv)
        //clockHandView = findViewById(R.id.material_clock_hand)
        ViewCompat.setAccessibilityLiveRegion(
            secondView, ViewCompat.ACCESSIBILITY_LIVE_REGION_ASSERTIVE
        )
        ViewCompat.setAccessibilityLiveRegion(
            minuteView,
            ViewCompat.ACCESSIBILITY_LIVE_REGION_ASSERTIVE
        )
        setupDoubleTap()
        setUpDisplay()
    }
}