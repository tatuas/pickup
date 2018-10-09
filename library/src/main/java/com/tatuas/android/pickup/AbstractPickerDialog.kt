package com.tatuas.android.pickup

import android.content.Context
import android.graphics.Typeface
import android.support.v7.app.AlertDialog
import android.view.View
import com.shawnlin.numberpicker.NumberPicker

abstract class AbstractPickerDialog protected constructor(protected val context: Context) {
    companion object {
        private const val FIRST_POSITION = 0
    }

    protected var cancelable: Boolean = false
    protected var positiveText = android.R.string.ok
    protected var negativeText = android.R.string.cancel

    protected var emptyText = android.R.string.unknownName
    protected var typeface: Typeface? = null
    protected var textSize: Int? = null
    protected var textColor: Int? = null
    protected var selectedTextSize: Int? = null
    protected var selectedTextColor: Int? = null

    abstract fun create(): AlertDialog

    protected fun createEmptyData() = listOf(context.getString(emptyText))

    protected fun numberPicker(parent: View, id: Int) =
            parent.findViewById<NumberPicker>(id).also { view ->
                textSize.takeIf { it != null }?.let { view.setTextSize(it) }
                textColor.takeIf { it != null }?.let { view.setTextColorResource(it) }

                selectedTextSize.takeIf { it != null }?.let { view.setSelectedTextSize(it) }
                selectedTextColor.takeIf { it != null }?.let { view.setSelectedTextColorResource(it) }

                typeface.takeIf { it != null }?.let { view.typeface = it }
            }!!

    protected fun NumberPicker.setData(data: List<String>) =
            also {
                displayedValues = null
                minValue = 0
                maxValue = data.size - 1
                displayedValues = data.toTypedArray()
            }

    protected fun NumberPicker.toFirstPosition(position: Int = FIRST_POSITION) =
            also {
                value = position
            }

    fun show() = create().show()
}
