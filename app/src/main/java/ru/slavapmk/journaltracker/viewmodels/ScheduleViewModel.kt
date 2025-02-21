package ru.slavapmk.journaltracker.viewmodels

import androidx.lifecycle.ViewModel
import java.util.Calendar
import java.util.Date

class ScheduleViewModel : ViewModel() {
    var currentDate: Date = Calendar.Builder().apply {
        set(Calendar.YEAR, 2024)
        set(Calendar.MONTH, 9)
        set(Calendar.DATE, 1)
    }.build().time
}