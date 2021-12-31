package com.example.improveyourplank_app.timepicker

import android.text.Editable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.google.android.material.textfield.TextInputLayout
import java.util.*


/**
 * A class for the keyboard logic when the TimePicker is in `TimeFormat.KEYBOARD`
 *
 *
 * Controls part of the input validation and focus switching.
 */
internal class TimePickerTextInputKeyController(
    private val minuteLayoutComboView: ChipTextInputComboView,
    private val secondLayoutComboView: ChipTextInputComboView,
    private val time: CustomTimeModel
) : OnEditorActionListener, View.OnKeyListener {
    private var keyListenerRunning = false

    /** Prepare Text inputs to receive key events and IME actions.  */
    fun bind() {
        val minuteLayout = minuteLayoutComboView.textInput
        val secondLayout = secondLayoutComboView.textInput
        val minuteEditText = minuteLayout.editText
        val secondEditText = secondLayout.editText
        minuteEditText!!.imeOptions = EditorInfo.IME_ACTION_NEXT or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        secondEditText!!.imeOptions =
            EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        minuteEditText.setOnEditorActionListener(this)
        minuteEditText.setOnKeyListener(this)
        secondEditText.setOnKeyListener(this)
    }

    private fun moveSelection(@TimePickerControls.ActiveSelection selection: Int) {
        secondLayoutComboView.setChecked(selection == Calendar.SECOND)
        minuteLayoutComboView.setChecked(selection == Calendar.MINUTE)
        time.selection = selection
    }

    override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        val actionNext = actionId == EditorInfo.IME_ACTION_NEXT
        if (actionNext) {
            moveSelection(Calendar.MINUTE)
        }
        return actionNext
    }

    override fun onKey(view: View, keyCode: Int, event: KeyEvent): Boolean {
        if (keyListenerRunning) {
            return false
        }
        keyListenerRunning = true
        val editText = view as EditText
        val ret = if (time.selection == Calendar.SECOND) onSecondKeyPress(
            keyCode,
            event,
            editText
        ) else onMinuteKeyPress(keyCode, event, editText)
        keyListenerRunning = false
        return ret
    }

    private fun onSecondKeyPress(keyCode: Int, event: KeyEvent, editText: EditText): Boolean {
        val switchFocus =
            keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && TextUtils.isEmpty(
                editText.text
            )
        if (switchFocus) {
            moveSelection(Calendar.MINUTE)
            return true
        }
        return false
    }

    private fun onMinuteKeyPress(keyCode: Int, event: KeyEvent, editText: EditText): Boolean {
        val text = editText.text ?: return false

        // Auto-switch focus when 2 numbers are successfully entered
        val switchFocus =
            keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 && event.action == KeyEvent.ACTION_UP && editText.selectionStart == 2 && text.length == 2
        if (switchFocus) {
            moveSelection(Calendar.SECOND)
            return true
        }
        return false
    }
}