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
import ru.slavapmk.journaltracker.databinding.ActivityCampusEditBinding
import ru.slavapmk.journaltracker.viewModels.CampusEditViewModel
import ru.slavapmk.journaltracker.ui.MainActivity.Companion.fmanager

class CampusEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCampusEditBinding
    val viewModel by viewModels<CampusEditViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivityCampusEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 5)
            insets
        }

        binding.codenameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.codename = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.name = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.codenameInput.setText(viewModel.codename)
        binding.nameInput.setText(viewModel.name)
    }
}