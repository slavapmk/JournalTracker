package ru.slavapmk.journalTracker.ui.selectWeek

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ru.slavapmk.journalTracker.R
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week
import ru.slavapmk.journalTracker.databinding.ActivitySelectWeekBinding
import ru.slavapmk.journalTracker.viewModels.SelectWeekViewModel
import ru.slavapmk.journalTracker.ui.MainActivity.Companion.fmanager
import java.time.DayOfWeek
import java.time.LocalDate

class SelectWeekActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectWeekBinding
    val viewModel by viewModels<SelectWeekViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        fmanager = supportFragmentManager
        binding = ActivitySelectWeekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.semester = Semester(
            2, 2, 2025,
            10, 6, 2025
        )
        viewModel.weeks = generateWeeks(viewModel.semester)

        binding.weeks.layoutManager = LinearLayoutManager(this)
        binding.weeks.adapter = SelectWeekAdapter(viewModel.weeks) {
            finish()
        }
    }
}

private fun generateWeeks(semester: Semester): List<Week> {
    val startDate = LocalDate.of(semester.startYear, semester.startMonth, semester.startDay)
    val endDate = LocalDate.of(semester.endYear, semester.endMonth, semester.endDay)

    val weeks = mutableListOf<Week>()
    var currentStart = startDate

    val firstWeekEnd = currentStart.with(DayOfWeek.SUNDAY).coerceAtMost(endDate)
    weeks.add(
        Week(
            startDay = currentStart.dayOfMonth,
            startMonth = currentStart.monthValue,
            startYear = currentStart.year,
            endDay = firstWeekEnd.dayOfMonth,
            endMonth = firstWeekEnd.monthValue,
            endYear = firstWeekEnd.year
        )
    )

    currentStart = firstWeekEnd.plusDays(1)

    while (currentStart.isBefore(endDate) || currentStart.isEqual(endDate)) {
        val currentEnd = currentStart.plusDays(6).coerceAtMost(endDate)

        weeks.add(
            Week(
                startDay = currentStart.dayOfMonth,
                startMonth = currentStart.monthValue,
                startYear = currentStart.year,
                endDay = currentEnd.dayOfMonth,
                endMonth = currentEnd.monthValue,
                endYear = currentEnd.year
            )
        )

        currentStart = currentEnd.plusDays(1)
    }

    return weeks
}