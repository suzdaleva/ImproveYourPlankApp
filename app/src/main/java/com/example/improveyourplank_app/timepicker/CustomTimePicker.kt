package com.example.improveyourplank_app.timepicker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityEventCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.resources.MaterialAttributes
import com.google.android.material.shape.MaterialShapeDrawable
import java.lang.IllegalArgumentException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.LinkedHashSet
import androidx.annotation.IntRange
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.timepicker.*
import org.jetbrains.annotations.NotNull
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.StyleRes
import com.example.improveyourplank_app.R


/** A [Dialog] with a clock display and a clock face to choose the time.  */
class CustomTimePicker : DialogFragment(),
    TimePickerView.OnDoubleTapListener {
    private val positiveButtonListeners: MutableSet<View.OnClickListener> = LinkedHashSet()
    private val negativeButtonListeners: MutableSet<View.OnClickListener> = LinkedHashSet()
    private val cancelListeners: MutableSet<DialogInterface.OnCancelListener> = LinkedHashSet()
    private val dismissListeners: MutableSet<DialogInterface.OnDismissListener> = LinkedHashSet()
    private var timePickerView: TimePickerView? = null
    private var textInputStub: ViewStub? = null

    private var timePickerTextInputPresenter: TimePickerTextInputPresenter? = null



    @StringRes
    private var titleResId = 0
    private var titleText: CharSequence? = null

    @StringRes
    private var positiveButtonTextResId = 0
    private var positiveButtonText: CharSequence? = null

    @StringRes
    private var negativeButtonTextResId = 0
    private var negativeButtonText: CharSequence? = null

    private var cancelButton: Button? = null


    private var time: CustomTimeModel? = null
    //private var overrideThemeResId = 0


    @get:IntRange(from = 0, to = 60)
    val second: Int
        get() = time!!.second

    /** Returns the hour of day in the range [0, 23].  */
    @get:IntRange(from = 0, to = 60)
    val minute: Int
        get() = time!!.minute

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        val context = dialog.context
//        val surfaceColor = MaterialAttributes.resolveOrThrow(
//            context, R.attr.colorSurface, CustomTimePicker::class.java.canonicalName
//        )
//        val background = MaterialShapeDrawable(
//            context,
//            null,
//            R.attr.materialTimePickerStyle,
//            R.style.Widget_MaterialComponents_TimePicker
//        )
//        val a = context.obtainStyledAttributes(
//            null,
//            R.styleable.MaterialTimePicker,
//            R.attr.materialTimePickerStyle,
//            R.style.Widget_MaterialComponents_TimePicker
//        )
//
//        a.recycle()
//        background.initializeElevationOverlay(context)
//        background.fillColor = ColorStateList.valueOf(surfaceColor)
//        window!!.setBackgroundDrawable(background)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        // On some Android APIs the dialog won't wrap content by default. Explicitly update here.
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        // This has to be done after requestFeature() is called on API <= 23.
//        background.elevation = ViewCompat.getElevation(window.decorView)
        return dialog
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        restoreState(bundle ?: arguments)
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.putParcelable(TIME_MODEL_EXTRA, time)
        bundle.putInt(TITLE_RES_EXTRA, titleResId)
        bundle.putCharSequence(TITLE_TEXT_EXTRA, titleText)
        bundle.putInt(POSITIVE_BUTTON_TEXT_RES_EXTRA, positiveButtonTextResId)
        bundle.putCharSequence(POSITIVE_BUTTON_TEXT_EXTRA, positiveButtonText)
        bundle.putInt(NEGATIVE_BUTTON_TEXT_RES_EXTRA, negativeButtonTextResId)
        bundle.putCharSequence(NEGATIVE_BUTTON_TEXT_EXTRA, negativeButtonText)
        //bundle.putInt(OVERRIDE_THEME_RES_ID, overrideThemeResId)
    }

    private fun restoreState(bundle: Bundle?) {
        if (bundle == null) {
            return
        }
        time = bundle.getParcelable(TIME_MODEL_EXTRA)
        if (time == null) {
            time = CustomTimeModel()
        }
        titleResId = bundle.getInt(TITLE_RES_EXTRA, 0)
        titleText = bundle.getCharSequence(TITLE_TEXT_EXTRA)
        positiveButtonTextResId = bundle.getInt(POSITIVE_BUTTON_TEXT_RES_EXTRA, 0)
        positiveButtonText = bundle.getCharSequence(POSITIVE_BUTTON_TEXT_EXTRA)
        negativeButtonTextResId = bundle.getInt(NEGATIVE_BUTTON_TEXT_RES_EXTRA, 0)
        negativeButtonText = bundle.getCharSequence(NEGATIVE_BUTTON_TEXT_EXTRA)
        //overrideThemeResId = bundle.getInt(OVERRIDE_THEME_RES_ID, 0)
    }

//    @SuppressLint("RestrictedApi")
//    private fun getThemeResId(): Int {
//        if (overrideThemeResId != 0) {
//            return overrideThemeResId
//        }
//        val value = MaterialAttributes.resolve(requireContext(), R.attr.materialTimePickerTheme)
//        return value?.data ?: 0
//    }

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?,
        bundle: Bundle?
    ): View {
        val root = layoutInflater.inflate(R.layout.timepicker_dialog, viewGroup) as ViewGroup
        timePickerView = root.findViewById(R.id.inputBackground)
        timePickerView!!.setOnDoubleTapListener(this)
        textInputStub = root.findViewById(R.id.textinputTimepicker)
        val headerTitle = root.findViewById<TextView>(R.id.dialogHeader)
        if (titleResId != 0) {
            headerTitle.setText(titleResId)
        } else if (!TextUtils.isEmpty(titleText)) {
            headerTitle.text = titleText
        }
        timePickerTextInputPresenter = initializePresenter(textInputStub)
        timePickerTextInputPresenter?.show()
        timePickerTextInputPresenter?.invalidate()
        val okButton = root.findViewById<Button>(R.id.dialogOkButton)
        okButton.setOnClickListener { v ->
            for (listener in positiveButtonListeners) {
                listener.onClick(v)
            }
            dismiss()
        }
        if (positiveButtonTextResId != 0) {
            okButton.setText(positiveButtonTextResId)
        } else if (!TextUtils.isEmpty(positiveButtonText)) {
            okButton.text = positiveButtonText
        }
        cancelButton = root.findViewById(R.id.dialogCancelButton)
        cancelButton!!.setOnClickListener(
            View.OnClickListener { v ->
                for (listener in negativeButtonListeners) {
                    listener.onClick(v)
                }
                dismiss()
            })
        if (negativeButtonTextResId != 0) {
            cancelButton!!.setText(negativeButtonTextResId)
        } else if (!TextUtils.isEmpty(negativeButtonText)) {
            cancelButton!!.setText(negativeButtonText)
        }
        val closeButton = root.findViewById<ImageView>(R.id.dialogCloseButton)
        closeButton!!.setOnClickListener(
            View.OnClickListener { v ->
                for (listener in negativeButtonListeners) {
                    listener.onClick(v)
                }
                dismiss()
            })
        updateCancelButtonVisibility()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timePickerTextInputPresenter = null
        if (timePickerView != null) {
            timePickerView!!.setOnDoubleTapListener(null)
            timePickerView = null
        }
    }

    override fun onCancel(dialogInterface: DialogInterface) {
        for (listener in cancelListeners) {
            listener.onCancel(dialogInterface)
        }
        super.onCancel(dialogInterface)
    }

    override fun onDismiss(dialogInterface: DialogInterface) {
        for (listener in dismissListeners) {
            listener.onDismiss(dialogInterface)
        }
        super.onDismiss(dialogInterface)
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        updateCancelButtonVisibility()
    }

    /** @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    override fun onDoubleTap() {
        timePickerTextInputPresenter = initializePresenter(textInputStub)
        timePickerTextInputPresenter?.show()
        timePickerTextInputPresenter?.invalidate()
        timePickerTextInputPresenter!!.resetChecked()
    }


    private fun updateCancelButtonVisibility() {
        if (cancelButton != null) {
            cancelButton!!.visibility = if (isCancelable) View.VISIBLE else View.GONE
        }
    }

    private fun initializePresenter(textInputStub: ViewStub?
    ): TimePickerTextInputPresenter? {
        if (timePickerTextInputPresenter == null) {
            val textInputView = textInputStub?.inflate() as LinearLayout
            timePickerTextInputPresenter = TimePickerTextInputPresenter(textInputView, time!!)
        }
        timePickerTextInputPresenter!!.clearCheck()
        return timePickerTextInputPresenter
    }

//    private fun dataForMode(): Int {
//                return R.string.material_timepicker_text_input_mode_description
//        }

    /** The supplied listener is called when the user confirms a valid selection.  */
    fun addOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean {
        return positiveButtonListeners.add(listener)
    }

    /**
     * Removes a listener previously added via [ ][MaterialTimePicker.addOnPositiveButtonClickListener].
     */
    fun removeOnPositiveButtonClickListener(listener: View.OnClickListener): Boolean {
        return positiveButtonListeners.remove(listener)
    }

    /**
     * Removes all listeners added via [ ][MaterialTimePicker.addOnPositiveButtonClickListener].
     */
    fun clearOnPositiveButtonClickListeners() {
        positiveButtonListeners.clear()
    }


    fun addOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean {
        return negativeButtonListeners.add(listener)
    }


    fun removeOnNegativeButtonClickListener(listener: View.OnClickListener): Boolean {
        return negativeButtonListeners.remove(listener)
    }


    fun clearOnNegativeButtonClickListeners() {
        negativeButtonListeners.clear()
    }


    fun addOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean {
        return cancelListeners.add(listener)
    }


    fun removeOnCancelListener(listener: DialogInterface.OnCancelListener): Boolean {
        return cancelListeners.remove(listener)
    }


    fun clearOnCancelListeners() {
        cancelListeners.clear()
    }


    fun addOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean {
        return dismissListeners.add(listener)
    }


    fun removeOnDismissListener(listener: DialogInterface.OnDismissListener): Boolean {
        return dismissListeners.remove(listener)
    }


    fun clearOnDismissListeners() {
        dismissListeners.clear()
    }

    fun newInstance(options: Builder): CustomTimePicker {
        val fragment = CustomTimePicker()
        val args = Bundle()
        args.putParcelable(TIME_MODEL_EXTRA, options.timeBuilder)

        args.putInt(TITLE_RES_EXTRA, options.titleTextResIdBuilder)
        if (options.titleTextBuilder != null) {
            args.putCharSequence(TITLE_TEXT_EXTRA, options.titleTextBuilder)
        }
        args.putInt(POSITIVE_BUTTON_TEXT_RES_EXTRA, options.positiveButtonTextResIdBuilder)
        if (options.positiveButtonTextBuilder != null) {
            args.putCharSequence(POSITIVE_BUTTON_TEXT_EXTRA, options.positiveButtonTextBuilder)
        }
        args.putInt(NEGATIVE_BUTTON_TEXT_RES_EXTRA, options.negativeButtonTextResIdBuilder)
        if (options.negativeButtonTextBuilder != null) {
            args.putCharSequence(NEGATIVE_BUTTON_TEXT_EXTRA, options.negativeButtonTextBuilder)
        }
        fragment.arguments = args
        return fragment
    }


    class Builder {
        var timeBuilder = CustomTimeModel()


        @StringRes
        var titleTextResIdBuilder = 0
        var titleTextBuilder: CharSequence? = null

        @StringRes
        var positiveButtonTextResIdBuilder = 0
        var positiveButtonTextBuilder: CharSequence? = null

        @StringRes
        var negativeButtonTextResIdBuilder = 0
        var negativeButtonTextBuilder: CharSequence? = null

        fun setMinute(@androidx.annotation.IntRange(from = 0, to = 60) minute: Int): Builder {
            timeBuilder.setMinute(minute)
            return this
        }


        fun setSecond(@androidx.annotation.IntRange(from = 0, to = 60) second: Int): Builder {
            timeBuilder.setSecond(second)
            return this
        }

        fun setTitleText(@StringRes titleTextResId: Int): Builder {
            this.titleTextResIdBuilder = titleTextResId
            return this
        }

        fun setTitleText(charSequence: CharSequence?): Builder {
            this.titleTextBuilder = charSequence
            return this
        }


        fun setPositiveButtonText(@StringRes positiveButtonTextResId: Int): Builder {
            this.positiveButtonTextResIdBuilder = positiveButtonTextResId
            return this
        }


        fun setPositiveButtonText(positiveButtonText: CharSequence?): Builder {
            this.positiveButtonTextBuilder = positiveButtonText
            return this
        }


        fun setNegativeButtonText(@StringRes negativeButtonTextResId: Int): Builder {
            this.negativeButtonTextResIdBuilder = negativeButtonTextResId
            return this
        }

        /**
         * Sets the text used in the negative action button.
         */
        fun setNegativeButtonText(negativeButtonText: CharSequence?): Builder {
            this.negativeButtonTextBuilder = negativeButtonText
            return this
        }


        /** Creates a [MaterialTimePicker] with the provided options.  */
        fun build(): CustomTimePicker {
            return CustomTimePicker().newInstance(this)
        }
    }



    companion object {

        const val TIME_MODEL_EXTRA = "TIME_PICKER_TIME_MODEL"
        const val TITLE_RES_EXTRA = "TIME_PICKER_TITLE_RES"
        const val TITLE_TEXT_EXTRA = "TIME_PICKER_TITLE_TEXT"
        const val POSITIVE_BUTTON_TEXT_RES_EXTRA = "TIME_PICKER_POSITIVE_BUTTON_TEXT_RES"
        const val POSITIVE_BUTTON_TEXT_EXTRA = "TIME_PICKER_POSITIVE_BUTTON_TEXT"
        const val NEGATIVE_BUTTON_TEXT_RES_EXTRA = "TIME_PICKER_NEGATIVE_BUTTON_TEXT_RES"
        const val NEGATIVE_BUTTON_TEXT_EXTRA = "TIME_PICKER_NEGATIVE_BUTTON_TEXT"

    }
}