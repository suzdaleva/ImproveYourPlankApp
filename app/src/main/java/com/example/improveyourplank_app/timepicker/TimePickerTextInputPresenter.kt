package com.example.improveyourplank_app.timepicker

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.example.improveyourplank_app.R
//import com.google.android.material.R
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.button.MaterialButtonToggleGroup.OnButtonCheckedListener
import com.google.android.material.color.MaterialColors
import com.google.android.material.internal.TextWatcherAdapter
import java.lang.NumberFormatException
import java.util.*
import com.example.improveyourplank_app.timepicker.ClickActionDelegate


internal class TimePickerTextInputPresenter(
    private val timePickerView: LinearLayout,
    private val time: CustomTimeModel
) :
    TimePickerView.OnSelectionChange, TimePickerPresenter {
    private val secondTextWatcher: TextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable) {
            try {
                if (TextUtils.isEmpty(s)) {
                    time.setSecond(0)
                    return
                }
                val second = s.toString().toInt()
                time.setSecond(second)
            } catch (ok: NumberFormatException) {
                // ignore invalid input
            }
        }
    }
    private val minuteTextWatcher: TextWatcher = object : TextWatcherAdapter() {
        override fun afterTextChanged(s: Editable) {
            try {
                if (TextUtils.isEmpty(s)) {
                    time.setMinute(0)
                    return
                }
                val minute = s.toString().toInt()
                time.setMinute(minute)
            } catch (ok: NumberFormatException) {
                // ignore invalid input
            }
        }
    }
    private val secondTextInput: ChipTextInputComboView
    private val minuteTextInput: ChipTextInputComboView
    private val controller: TimePickerTextInputKeyController
    private val secondEditText: EditText?
    private val minuteEditText: EditText?
    override fun initialize() {
        addTextWatchers()
        setTime(time)
        controller.bind()
    }

    private fun addTextWatchers() {
        minuteEditText!!.addTextChangedListener(minuteTextWatcher)
        secondEditText!!.addTextChangedListener(secondTextWatcher)
    }

    private fun removeTextWatchers() {
        minuteEditText!!.removeTextChangedListener(minuteTextWatcher)
        secondEditText!!.removeTextChangedListener(secondTextWatcher)
    }

    private fun setTime(time: CustomTimeModel) {
        removeTextWatchers()
        val current = timePickerView.resources.configuration.locale
        val secondFormatted = String.format(current, "%02d", time.second)
        val minuteFormatted = String.format(current, "%02d", time.minute)
        secondTextInput.setText(secondFormatted)
        minuteTextInput.setText(minuteFormatted)
        addTextWatchers()
        //updateSelection()
    }

//    private fun setupPeriodToggle() {
//        toggle = timePickerView.findViewById(R.id.material_clock_period_toggle)
//        toggle!!.addOnButtonCheckedListener(
//            OnButtonCheckedListener { group, checkedId, isChecked ->
//                val period =
//                    if (checkedId == R.id.material_clock_period_pm_button) Calendar.PM else Calendar.AM
//                time.setPeriod(period)
//            })
//        toggle!!.visibility = View.VISIBLE
//        updateSelection()
//    }

//    private fun updateSelection() {
//        if (toggle == null) {
//            return
//        }
//        toggle!!.check(
//            if (time.period == Calendar.AM) R.id.material_clock_period_am_button else R.id.material_clock_period_pm_button
//        )
//    }

    override fun onSelectionChanged(selection: Int) {
        time.selection = selection
        secondTextInput.setChecked(selection == Calendar.SECOND)
        minuteTextInput.setChecked(selection == Calendar.MINUTE)
        //updateSelection()
    }

    override fun show() {
        timePickerView.visibility = View.VISIBLE
    }

    override fun hide() {
        val currentFocus = timePickerView.focusedChild
        // Hide keyboard in case it was showing.
        if (currentFocus == null) {
            timePickerView.visibility = View.GONE
            return
        }
        val context = timePickerView.context
        val imm = ContextCompat.getSystemService(
            context,
            InputMethodManager::class.java
        )
        imm?.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        timePickerView.visibility = View.GONE
    }

    override fun invalidate() {
        setTime(time)
    }

    fun resetChecked() {
        minuteTextInput.setChecked(time.selection == Calendar.MINUTE)
        secondTextInput.setChecked(time.selection == Calendar.SECOND)
    }

    fun clearCheck() {
        secondTextInput.setChecked(false)
        minuteTextInput.setChecked(false)
    }

    companion object {

        @SuppressLint("SoonBlockedPrivateApi")
        private fun setCursorDrawableColor(view: EditText?, @ColorInt color: Int) {
            try {
                val context = view!!.context
                val cursorDrawableResField =
                    TextView::class.java.getDeclaredField("mCursorDrawableRes")
                cursorDrawableResField.isAccessible = true
                val cursorDrawableResId = cursorDrawableResField.getInt(view)
                val editorField = TextView::class.java.getDeclaredField("mEditor")
                editorField.isAccessible = true
                val editor = editorField[view]
                val clazz: Class<*> = editor.javaClass
                val cursorDrawableField = clazz.getDeclaredField("mCursorDrawable")
                cursorDrawableField.isAccessible = true
                val drawable = AppCompatResources.getDrawable(context, cursorDrawableResId)
                drawable!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                val drawables = arrayOf<Drawable?>(drawable, drawable)
                cursorDrawableField[editor] = drawables
            } catch (ignored: Throwable) {
                // ignore use the drawable default color (black).
            }
        }
    }

    init {
        val res = timePickerView.resources
        secondTextInput = timePickerView.findViewById(R.id.material_minute_text_input)
        minuteTextInput = timePickerView.findViewById(R.id.material_hour_text_input)
        minuteTextInput.setTag(R.id.selection_type, Calendar.MINUTE)
        secondTextInput.setTag(R.id.selection_type, Calendar.SECOND)

        val onClickListener =
            View.OnClickListener { v -> onSelectionChanged(v.getTag(R.id.selection_type) as Int) }
        secondTextInput.setOnClickListener(onClickListener)
        minuteTextInput.setOnClickListener(onClickListener)
        secondTextInput.addInputFilter(time.secondInputValidator)
        minuteTextInput.addInputFilter(time.minuteInputValidator)
        secondEditText = secondTextInput.textInput.editText
        minuteEditText = minuteTextInput.textInput.editText
        if (VERSION.SDK_INT < VERSION_CODES.LOLLIPOP) {
            // Our XML drawable is not colored for pre-lollipop, set color programmatically.
            val primaryColor = MaterialColors.getColor(timePickerView, R.attr.colorPrimary)
            setCursorDrawableColor(secondEditText, primaryColor)
            setCursorDrawableColor(minuteEditText, primaryColor)
        }
        controller = TimePickerTextInputKeyController(
            secondTextInput, minuteTextInput,
            time
        )
        minuteTextInput.setChipDelegate(
            ClickActionDelegate(
                timePickerView.context,
                R.string.material_minute_selection
            )
        )
        secondTextInput.setChipDelegate(
            ClickActionDelegate(
                timePickerView.context, R.string.material_second_selection
            )
        )
        initialize()
    }
}