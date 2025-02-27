package ru.slavapmk.journalTracker.ui.semesters

import android.app.DatePickerDialog
import android.os.Bundle
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
import java.util.Calendar
import java.util.Locale

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

        binding.startDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = String.format(
                        Locale.getDefault(),
                        "%02d.%02d.%02d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear % 100
                    )
                    viewModel.startYear = selectedYear
                    viewModel.startMonth = selectedMonth + 1
                    viewModel.startDay = selectedDay
                    binding.startDateInput.setText(date)
                },
                year, month, day
            )

            datePickerDialog.show()
        }
        if (viewModel.startDay != null || viewModel.startMonth != null || viewModel.startYear != null) {
            binding.startDateInput.setText(
                String.format(
                    Locale.getDefault(),
                    "%02d.%02d.%02d",
                    viewModel.startDay,
                    viewModel.startMonth,
                    viewModel.startYear?.rem(100)
                )
            )
        }

        binding.endDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = String.format(
                        Locale.getDefault(),
                        "%02d.%02d.%02d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear % 100
                    )
                    viewModel.endYear = selectedYear
                    viewModel.endMonth = selectedMonth + 1
                    viewModel.endDay = selectedDay
                    binding.endDateInput.setText(date)
                },
                year, month, day
            )

            datePickerDialog.show()
        }
        if (viewModel.endDay != null || viewModel.endMonth != null || viewModel.endYear != null) {
            binding.endDateInput.setText(
                String.format(
                    Locale.getDefault(),
                    "%02d.%02d.%02d",
                    viewModel.endDay,
                    viewModel.endMonth,
                    viewModel.endYear?.rem(100)
                )
            )
        }

        binding.semesters.layoutManager = LinearLayoutManager(this)
        binding.semesters.adapter = semestersAdapter
    }
}