package com.example.improveyourplank_app.timepicker

internal interface TimePickerPresenter {
    /** Do any final intialization  */
    fun initialize()

    /** Refresh the data in the view based on the model  */
    fun invalidate()
    fun hide()
    fun show()
}
