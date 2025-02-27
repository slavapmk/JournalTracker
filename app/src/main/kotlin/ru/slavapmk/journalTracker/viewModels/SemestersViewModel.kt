package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.semesters.Semester

class SemestersViewModel : ViewModel() {
    var startDate: String = ""
    var endDate: String = ""
    val semesters: MutableList<Semester> = mutableListOf()
}