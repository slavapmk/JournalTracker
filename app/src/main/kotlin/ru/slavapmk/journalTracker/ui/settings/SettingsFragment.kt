package ru.slavapmk.journalTracker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.settings.StudentsFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats
import ru.slavapmk.journalTracker.databinding.FragmentSettingsBinding
import ru.slavapmk.journalTracker.ui.MainActivity
import ru.slavapmk.journalTracker.ui.campusEdit.CampusEditActivity
import ru.slavapmk.journalTracker.ui.studentsedit.StudentsEditActivity
import ru.slavapmk.journalTracker.ui.timeEdit.TimeEditActivity
import ru.slavapmk.journalTracker.viewModels.SettingsViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val activity: MainActivity by lazy { requireActivity() as MainActivity }
    val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        binding.lessonsButton.setOnClickListener {
            val intent = Intent(activity, TimeEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.campusButton.setOnClickListener {
            val intent = Intent(activity, CampusEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.studentsButton.setOnClickListener {
            val intent = Intent(activity, StudentsEditActivity::class.java)
            activity.startActivity(intent)
        }

        binding.groupInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.groupName = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.groupInput.setText(viewModel.groupName)

        val weeksFormatTypes = listOf(
            getString(R.string.week_format_type_even_uneven),
            getString(R.string.week_format_type_up_down)
        )
        val weeksAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, weeksFormatTypes
        )
        val weeksFormatInput = binding.weeksFormatInput
        weeksFormatInput.setAdapter(weeksAdapter)
        weeksFormatInput.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> viewModel.weekFormat = WeeksFormats.EVEN_UNEVEN
                1 -> viewModel.weekFormat = WeeksFormats.UP_DOWN
                else -> throw IllegalStateException()
            }
        }
        binding.weeksFormatInput.setText(
            when (viewModel.weekFormat) {
                WeeksFormats.EVEN_UNEVEN -> weeksFormatTypes[0]
                WeeksFormats.UP_DOWN -> weeksFormatTypes[1]
            }
        )

        val studentsFormatTypes = listOf(
            getString(R.string.students_format_type_plus_minus),
            getString(R.string.students_format_type_skip_hours)
        )
        val studentsAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, studentsFormatTypes
        )
        val studentsFormatInput = binding.studentsFormatInput
        studentsFormatInput.setAdapter(studentsAdapter)
        studentsFormatInput.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> viewModel.studentFormat = StudentsFormats.PLUS_MINUS
                1 -> viewModel.studentFormat = StudentsFormats.SKIP_HOURS
                else -> throw IllegalStateException()
            }
        }
        binding.studentsFormatInput.setText(
            when (viewModel.studentFormat) {
                StudentsFormats.PLUS_MINUS -> R.string.students_format_type_plus_minus
                StudentsFormats.SKIP_HOURS -> R.string.students_format_type_skip_hours
            }
        )

        val weeksCountTypes = listOf("1", "2")
        val weeksCountAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, weeksCountTypes
        )
        val weeksCountInput = binding.weeksInput
        weeksCountInput.setAdapter(weeksCountAdapter)
        weeksCountInput.setOnItemClickListener { _, _, position, _ ->
            position + 1
        }
        binding.studentsFormatInput.setText(
            getString(R.string.settings_weeks_types, viewModel.weekTypes)
        )

        return binding.root
    }
}