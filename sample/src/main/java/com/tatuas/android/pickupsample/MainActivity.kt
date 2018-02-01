package com.tatuas.android.pickupsample

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.tatuas.android.pickup.MultiplePickerDialog
import com.tatuas.android.pickup.SteppingPickerDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var steppingPickerDialog: AlertDialog? = null
    private var multiplePickerDialog: AlertDialog? = null
    private var multiplePickerDialogPositiveButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val steppingChildren1 = listOf(
                Child(0, "child1-1"),
                Child(1, "child1-2"),
                Child(2, "child1-3"))

        val steppingChildren2 = listOf(
                Child(0, "child2-1"),
                Child(1, "child2-2"),
                Child(2, "child2-3"))

        val steppingParents = (1..100).map {
            Parent(it, "Parent$it", if (it % 2 != 0) steppingChildren1 else steppingChildren2)
        }

        val multipleItems = (1..100).map { "value$it" }.toList()

        steppingPickerDialog = SteppingPickerDialog.newInstance(this)
                .withCancelable(false)
                .withTypeFace(Typeface.DEFAULT_BOLD)
                .withTextSize(R.dimen.abc_text_size_medium_material)
                .withTextColor(android.R.color.darker_gray)
                .withSelectedTextSize(R.dimen.abc_text_size_large_material)
                .withSelectedTextColor(R.color.colorAccent)
                .withEmptyText(R.string.abc_action_mode_done)
                .withPrimaryPosition(SteppingPickerDialog.PrimaryPosition.LEFT)
                .withPrimaryData(steppingParents.map { it.value })
                .withPrimaryPicked {
                    val c = steppingParents[it].children
                    if (c != null && c.isNotEmpty())
                        c.map { it.value } else null
                }
                .withPositiveClicked {
                    val f = steppingParents[it.first]
                    val s = f.children?.get(it.second)
                    Toast.makeText(this, "${f.value}, ${s?.value}", Toast.LENGTH_SHORT).show()
                }
                .create()

        multiplePickerDialog = MultiplePickerDialog.newInstance(this)
                .withCancelable(false)
                .withTypeFace(Typeface.DEFAULT_BOLD)
                .withTextSize(R.dimen.abc_text_size_medium_material)
                .withTextColor(android.R.color.darker_gray)
                .withSelectedTextSize(R.dimen.abc_text_size_large_material)
                .withSelectedTextColor(R.color.colorAccent)
                .withEmptyText(R.string.abc_action_mode_done)
                .withLeftData(multipleItems)
                .withCenterData(multipleItems)
                .withRightData(multipleItems)
                .withPickerChanged {
                    multiplePickerDialogPositiveButton?.isEnabled =
                            (it.first == it.second && it.second == it.third)
                }
                .withPositiveClicked {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
                .create()

        multiplePickerDialog?.setOnShowListener {
            multiplePickerDialogPositiveButton = multiplePickerDialog
                    ?.getButton(AlertDialog.BUTTON_POSITIVE)
            multiplePickerDialogPositiveButton?.isEnabled = false
        }

        hello1.setOnClickListener {
            steppingPickerDialog?.show()
        }

        hello2.setOnClickListener {
            multiplePickerDialog?.show()
        }
    }

    override fun onDestroy() {
        steppingPickerDialog?.dismiss()
        multiplePickerDialog?.dismiss()

        steppingPickerDialog = null
        multiplePickerDialog = null
        multiplePickerDialogPositiveButton = null

        super.onDestroy()
    }

    private data class Parent(val id: Int, val value: String, val children: List<Child>?)

    private data class Child(val id: Int, val value: String)
}
