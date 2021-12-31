package com.example.improveyourplank_app.timepicker

import android.annotation.SuppressLint
import androidx.annotation.IntRange
import android.content.res.Resources
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import com.example.improveyourplank_app.timepicker.TimePickerControls.ClockPeriod
import java.util.*



/** The representation of the TimeModel used by TimePicker views.  */
class CustomTimeModel(
    internal var second: Int, internal var minute: Int, @TimePickerControls.ActiveSelection var selection: Int
) :
    Parcelable {
    internal var minuteInputValidator: MaxInputValidator = MaxInputValidator(59)
    internal  var secondInputValidator: MaxInputValidator = MaxInputValidator(59)


    @JvmOverloads
    constructor() : this(
        0,
        0,
        Calendar.MINUTE,
    )

    protected constructor(`in`: Parcel) : this(
        `in`.readInt(),
        `in`.readInt(),
        `in`.readInt(),
    ) {
    }
    @SuppressLint("SupportAnnotationUsage")
    @IntRange(from = 0, to = 60)
    fun setMinute(minute: Int) {
        this.minute = minute % 60
    }

    @SuppressLint("SupportAnnotationUsage")
    @IntRange(from = 0, to = 60)
    fun setSecond(second: Int) {
        this.second = second % 60
    }

    override fun hashCode(): Int {
        val hashedFields = arrayOf<Any>(
            second,
            minute,
            selection
        )
        return Arrays.hashCode(hashedFields)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is CustomTimeModel) {
            return false
        }
        val that = o
        return second == that.second && minute == that.minute &&  selection == that.selection
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(second)
        dest.writeInt(minute)
        dest.writeInt(selection)
    }


    companion object {
        const val ZERO_LEADING_NUMBER_FORMAT = "%02d"
        const val NUMBER_FORMAT = "%d"

        @SuppressLint("ParcelCreator")
        val CREATOR: Creator<CustomTimeModel?> = object : Creator<CustomTimeModel?> {
            override fun createFromParcel(`in`: Parcel): CustomTimeModel? {
                return CustomTimeModel(`in`)
            }

            override fun newArray(size: Int): Array<CustomTimeModel?> {
                return arrayOfNulls(size)
            }
        }

        @JvmOverloads
        fun formatText(
            resources: Resources,
            text: CharSequence,
            format: String? = ZERO_LEADING_NUMBER_FORMAT
        ): String {
            return String.format(
                resources.configuration.locale,
                format!!, text.toString().toInt()
            )
        }
    }

}