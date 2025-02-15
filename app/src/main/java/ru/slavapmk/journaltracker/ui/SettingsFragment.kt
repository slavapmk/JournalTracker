package ru.slavapmk.journaltracker.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.slavapmk.journaltracker.databinding.FragmentSettingsBinding
import ru.slavapmk.journaltracker.models.SettingsViewModel

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
        binding.weeksInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.weekTypes = s.toString().toInt()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.weeksFormatInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.weekFormat = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.studentsFormatInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.studentFormat = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.groupInput.setText(viewModel.groupName)
        binding.weeksInput.setText(viewModel.weekTypes.toString())
        binding.weeksFormatInput.setText(viewModel.weekFormat)
        binding.studentsFormatInput.setText(viewModel.studentFormat)

        return binding.root
    }
}