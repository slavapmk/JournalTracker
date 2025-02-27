package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.semesters.Semester

class SemestersViewModel : ViewModel() {
    var startDay: Int? = null
    var startMonth: Int? = null
    var startYear: Int? = null
    var endDay: Int? = null
    var endMonth: Int? = null
    var endYear: Int? = null
    val semesters: MutableList<Semester> = mutableListOf()
}