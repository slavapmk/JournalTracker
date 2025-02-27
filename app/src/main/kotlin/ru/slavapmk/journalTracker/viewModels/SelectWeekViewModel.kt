package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.selectWeek.Semester
import ru.slavapmk.journalTracker.dataModels.selectWeek.Week

class SelectWeekViewModel : ViewModel() {
    lateinit var semester: Semester
    lateinit var weeks: List<Week>
}