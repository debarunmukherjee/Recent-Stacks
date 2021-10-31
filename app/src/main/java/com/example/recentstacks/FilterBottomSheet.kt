package com.example.recentstacks

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.example.recentstacks.viewmodels.QuestionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet(
    private val mainContext: Context,
    private val questionViewModel: QuestionViewModel
) : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "FilterBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filter_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val chosenItem = questionViewModel.chosenFilter.value
        val radioGroup = view?.findViewById<RadioGroup>(R.id.rgTagFilter)
        questionViewModel.allTags.observe(mainContext as LifecycleOwner, {
            if (it.isNotEmpty() && chosenItem != null && radioGroup != null) {
                // This will keep track of the total number of filters rendered.
                // This is needed because otherwise the total number of possible filters are becoming too high.
                // Hence, let's keep 10 filters at max.
                for ((index, tag) in it.withIndex()) {
                    addRadioButton(radioGroup, tag, chosenItem)
                    if (index > 8) {
                        break
                    }
                }
                addRadioButton(radioGroup, "All", chosenItem)
                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    val option = view?.findViewById<RadioButton>(checkedId)?.text.toString()
                    questionViewModel.updateChosenFilter(option)
                    Toast.makeText(mainContext, "Results for $option", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun addRadioButton(radioGroup: RadioGroup, option: String, chosenItem: String) {
        val radioButton = RadioButton(mainContext)
        radioButton.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        radioButton.text = option
        radioButton.id = View.generateViewId()
        radioButton.isChecked = chosenItem == option
        radioGroup.addView(radioButton)
    }
}