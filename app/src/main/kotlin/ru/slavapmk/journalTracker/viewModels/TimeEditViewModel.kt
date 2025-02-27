package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.timeEdit.TimeEditItem

class TimeEditViewModel : ViewModel() {
    var startHours: Int = -1
    var startMinutes: Int = -1
    var endHours: Int = -1
    var endMinutes: Int = -1
    var timeList: MutableList<TimeEditItem> = mutableListOf()
}