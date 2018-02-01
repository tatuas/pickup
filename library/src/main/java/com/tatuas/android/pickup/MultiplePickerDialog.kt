package com.tatuas.android.pickup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.shawnlin.numberpicker.NumberPicker

class MultiplePickerDialog(context: Context) : AbstractPickerDialog(context) {
    companion object {
        private const val UNDEFINED_POSITION = -1

        @JvmStatic
        fun newInstance(context: Context): MultiplePickerDialog {
            return MultiplePickerDialog(context)
        }
    }

    private var leftData: List<String> = listOf()
    private var centerData: List<String> = listOf()
    private var rightData: List<String> = listOf()

    private var onPositiveClicked: ((Triple<Int, Int, Int>) -> Unit)? = null
    private var onPickerChanged: ((Triple<Int, Int, Int>) -> Unit)? = null

    fun withCancelable(value: Boolean) = also { cancelable = value }

    fun withTypeFace(value: Typeface) = also { typeface = value }

    fun withTextSize(@DimenRes id: Int) = also { textSize = id }

    fun withTextColor(@ColorRes id: Int) = also { textColor = id }

    fun withSelectedTextSize(@DimenRes id: Int) = also { selectedTextSize = id }

    fun withSelectedTextColor(@ColorRes id: Int) = also { selectedTextColor = id }

    fun withPositiveText(@StringRes id: Int) = also { positiveText = id }

    fun withNegativeText(@StringRes id: Int) = also { negativeText = id }

    fun withEmptyText(@StringRes id: Int) = also { emptyText = id }

    fun withLeftData(data: List<String>) = also { leftData = data }

    fun withCenterData(data: List<String>) = also { centerData = data }

    fun withRightData(data: List<String>) = also { rightData = data }

    fun withPositiveClicked(impl: ((Triple<Int, Int, Int>) -> Unit)) = also { onPositiveClicked = impl }

    fun withPickerChanged(impl: ((Triple<Int, Int, Int>) -> Unit)) = also { onPickerChanged = impl }

    @SuppressLint("InflateParams")
    override fun create(): AlertDialog {
        val parent = LayoutInflater.from(context)
                .inflate(R.layout.dialog_multiple_picker, null, false)

        val left = numberPicker(parent, R.id.picker_left).setupWith(leftData)
        val center = numberPicker(parent, R.id.picker_center).setupWith(centerData)
        val right = numberPicker(parent, R.id.picker_right).setupWith(rightData)

        listOf(left, center, right).map {
            it.setOnValueChangedListener { _, _, _ ->
                val result = Triple(
                        if (leftData.isNotEmpty()) left.value else UNDEFINED_POSITION,
                        if (centerData.isNotEmpty()) center.value else UNDEFINED_POSITION,
                        if (rightData.isNotEmpty()) right.value else UNDEFINED_POSITION)

                onPickerChanged?.invoke(result)
            }
        }

        return AlertDialog.Builder(context)
                .setView(parent)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText, { _, _ ->
                    val result = Triple(
                            if (leftData.isNotEmpty()) left.value else UNDEFINED_POSITION,
                            if (centerData.isNotEmpty()) center.value else UNDEFINED_POSITION,
                            if (rightData.isNotEmpty()) right.value else UNDEFINED_POSITION)

                    onPositiveClicked?.invoke(result)
                })
                .setNegativeButton(negativeText, null)
                .create()
    }

    private fun NumberPicker.setupWith(data: List<String>): NumberPicker {
        return if (data.isNotEmpty()) {
            visibility = View.VISIBLE
            setData(data)
            toFirstPosition()
        } else {
            visibility = View.GONE
            toFirstPosition()
        }
    }
}
