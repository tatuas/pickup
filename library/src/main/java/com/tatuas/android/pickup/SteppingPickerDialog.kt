package com.tatuas.android.pickup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.shawnlin.numberpicker.NumberPicker

class SteppingPickerDialog(context: Context) : AbstractPickerDialog(context) {
    companion object {
        private const val UNDEFINED_POSITION = -1

        @JvmStatic
        fun newInstance(context: Context): SteppingPickerDialog {
            return SteppingPickerDialog(context)
        }
    }

    enum class PrimaryPosition {
        LEFT, RIGHT
    }

    private var primaryPosition = PrimaryPosition.LEFT
    private var primaryData: List<String> = listOf()
    private var onPrimaryPicked: ((Int) -> List<String>?)? = null
    private var onPositiveClicked: ((Pair<Int, Int>) -> Unit)? = null
    private var primaryDataFirstPosition: Int = UNDEFINED_POSITION
    private var secondaryDataFirstPosition: Int = UNDEFINED_POSITION

    fun withCancelable(value: Boolean) = also { cancelable = value }

    fun withTypeFace(value: Typeface) = also { typeface = value }

    fun withTextSize(@DimenRes id: Int) = also { textSize = id }

    fun withTextColor(@ColorRes id: Int) = also { textColor = id }

    fun withSelectedTextSize(@DimenRes id: Int) = also { selectedTextSize = id }

    fun withSelectedTextColor(@ColorRes id: Int) = also { selectedTextColor = id }

    fun withPositiveText(@StringRes id: Int) = also { positiveText = id }

    fun withNegativeText(@StringRes id: Int) = also { negativeText = id }

    fun withEmptyText(@StringRes id: Int) = also { emptyText = id }

    fun withPrimaryPosition(value: PrimaryPosition) = also { primaryPosition = value }

    fun withPrimaryData(data: List<String>) = also { primaryData = data }

    fun withPrimaryPicked(impl: ((Int) -> List<String>?)?) = also { onPrimaryPicked = impl }

    fun withPrimaryDataFirstPosition(firstPosition: Int) = also { primaryDataFirstPosition = firstPosition}

    fun withSecondaryDataFirstPosition(firstPosition: Int) = also { secondaryDataFirstPosition = firstPosition}

    fun withPositiveClicked(impl: ((Pair<Int, Int>) -> Unit)?) = also { onPositiveClicked = impl }

    @SuppressLint("InflateParams")
    override fun create(): AlertDialog {
        val pickerViewIds = when (primaryPosition) {
            PrimaryPosition.LEFT -> Pair(R.id.picker_left, R.id.picker_right)
            PrimaryPosition.RIGHT -> Pair(R.id.picker_right, R.id.picker_left)
        }

        // setup view
        val parent = LayoutInflater.from(context)
                .inflate(R.layout.dialog_stepping_picker, null, false)
        val primaryView = numberPicker(parent, pickerViewIds.first)
        val secondaryView = numberPicker(parent, pickerViewIds.second)

        primaryView.setOnValueChangedListener { _, _, newPosition ->
            updateSecondaryData(secondaryView = secondaryView, primaryDataPosition = newPosition)
        }

        // setup builder
        val builder = AlertDialog.Builder(context)
                .setView(parent)
                .setCancelable(cancelable)
                .setPositiveButton(positiveText, { _, _ ->
                    onPositiveClicked?.invoke(Pair(primaryView.value, secondaryView.value))
                })
                .setNegativeButton(negativeText, null)

        // setup default data
        updatePrimaryData(primaryView = primaryView, primaryData = primaryData)
        updateSecondaryData(secondaryView = secondaryView, primaryDataPosition = primaryView.value)

        return builder.create()
    }

    private fun updatePrimaryData(primaryView: NumberPicker, primaryData: List<String>) {
        val data = if (primaryData.isNotEmpty()) primaryData else createEmptyData()
        primaryView.setData(data).run {
            if (primaryDataFirstPosition != UNDEFINED_POSITION) {
                toFirstPosition(primaryDataFirstPosition)
            } else {
                toFirstPosition()
            }
        }
    }

    private fun updateSecondaryData(secondaryView: NumberPicker, primaryDataPosition: Int) {
        val data = onPrimaryPicked?.invoke(primaryDataPosition) ?: createEmptyData()
        secondaryView.setData(data).run {
            if (primaryDataFirstPosition != UNDEFINED_POSITION) {
                toFirstPosition(primaryDataFirstPosition)
            } else {
                toFirstPosition()
            }
        }
    }
}
