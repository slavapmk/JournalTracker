package ru.slavapmk.journalTracker.ui.semesters

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.databinding.ActivitySemestersBinding
import ru.slavapmk.journalTracker.viewModels.SemestersViewModel
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import ru.slavapmk.journalTracker.ui.studentsedit.StudentsEditListAdapter

class SemestersActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySemestersBinding
    val viewModel by viewModels<SemestersViewModel>()

    private val semestersAdapter by lazy {
        SemestersAdapter(viewModel.semesters) { semester ->
            val indexOf = viewModel.semesters.indexOf(semester)
            val size = viewModel.semesters.size
            viewModel.semesters.remove(semester)
            val updateCount = size - indexOf
            binding.semesters.adapter?.notifyItemRemoved(indexOf)
            binding.semesters.adapter?.notifyItemRangeChanged(
                indexOf,
                updateCount
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivitySemestersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.startTimeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.startDate = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.endTimeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.endDate = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.startTimeInput.setText(viewModel.startDate)
        binding.endTimeInput.setText(viewModel.endDate)

        binding.semesters.layoutManager = LinearLayoutManager(this)
        binding.semesters.adapter = semestersAdapter
    }
}