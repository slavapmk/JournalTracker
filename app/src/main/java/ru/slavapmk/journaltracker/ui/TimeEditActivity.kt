package ru.slavapmk.journaltracker.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.slavapmk.journaltracker.R
import ru.slavapmk.journaltracker.databinding.ActivityTimeEditBinding
import ru.slavapmk.journaltracker.viewmodels.TimeEditViewModel
import ru.slavapmk.journaltracker.ui.MainActivity.Companion.fmanager

class TimeEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeEditBinding
    val viewModel by viewModels<TimeEditViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityTimeEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startTimeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.startTime = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        binding.endTimeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.endTime = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.startTimeInput.setText(viewModel.startTime)
        binding.endTimeInput.setText(viewModel.startTime)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5)
            insets
        }
    }
}