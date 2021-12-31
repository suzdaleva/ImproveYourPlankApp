package com.example.improveyourplank_app.timepicker

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Checkable
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import com.google.android.material.R
import com.google.android.material.chip.Chip
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.internal.ViewUtils
import com.google.android.material.textfield.TextInputLayout
import java.util.*


/**
 * A [Chip] that can switch to a [TextInputLayout] when checked to modify it's content.
 * It keeps the helper text from the TextInput always visible.
 */
internal class ChipTextInputComboView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr), Checkable {
    private val chip: Chip
    val textInput: TextInputLayout
    private val editText: EditText?
    private val watcher: TextWatcher
    //private val label: TextView
    private fun updateHintLocales() {
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            val configuration = context.resources.configuration
            val locales = configuration.locales
            editText!!.imeHintLocales = locales
        }
    }

    override fun isChecked(): Boolean {
        return chip.isChecked
    }

    @SuppressLint("RestrictedApi")
    override fun setChecked(checked: Boolean) {
        chip.isChecked = checked
        editText!!.visibility = if (checked) VISIBLE else INVISIBLE
        chip.visibility = if (checked) GONE else VISIBLE
        if (isChecked) {
            ViewUtils.requestFocusAndShowKeyboard(editText)
            if (!TextUtils.isEmpty(editText.text)) {
                editText.setSelection(editText.text.length)
            }
        }
    }

    override fun toggle() {
        chip.toggle()
    }

    fun setText(text: CharSequence) {
        chip.text = formatText(text)
        if (!TextUtils.isEmpty(editText!!.text)) {
            editText.removeTextChangedListener(watcher)
            editText.text = null
            editText.addTextChangedListener(watcher)
        }
    }

    private fun formatText(text: CharSequence): String {
        return CustomTimeModel.formatText(resources, text)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        chip.setOnClickListener(l)
    }

    override fun setTag(key: Int, tag: Any) {
        chip.setTag(key, tag)
    }

    fun setHelperText(helperText: CharSequence?) {
        //label.text = helperText
    }

    fun setCursorVisible(visible: Boolean) {
        editText!!.isCursorVisible = visible
    }

    fun addInputFilter(filter: InputFilter) {
        val current = editText!!.filters
        val arr = Arrays.copyOf(current, current.size + 1)
        arr[current.size] = filter
        editText.filters = arr
    }

    fun setChipDelegate(clickActionDelegate: AccessibilityDelegateCompat?) {
        ViewCompat.setAccessibilityDelegate(chip, clickActionDelegate)
    }

    @SuppressLint("RestrictedApi")
    private inner class TextFormatter : TextWatcherAdapter() {
        override fun afterTextChanged(editable: Editable) {
            if (TextUtils.isEmpty(editable)) {
                chip.text = formatText(this.DEFAULT_TEXT)
                return
            }
            chip.text = formatText(editable)
            chip.setTextColor(Color.BLACK)
        }

            val DEFAULT_TEXT = "00"

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateHintLocales()
    }

    init {
        val inflater = LayoutInflater.from(context)
        chip = inflater.inflate(R.layout.material_time_chip, this, false) as Chip
        textInput = inflater.inflate(R.layout.material_time_input, this, false) as TextInputLayout
        editText = textInput.editText
        editText!!.visibility = INVISIBLE
        watcher = TextFormatter()
        editText.addTextChangedListener(watcher)
        updateHintLocales()
        addView(chip)
        addView(textInput)
        //label = findViewById(R.id.material_label)
        editText.isSaveEnabled = false
    }
}