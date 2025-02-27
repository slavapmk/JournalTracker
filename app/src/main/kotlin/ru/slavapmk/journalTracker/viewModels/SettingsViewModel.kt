package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.settings.AttendanceFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats

class SettingsViewModel : ViewModel() {
    var groupName = ""
    var weekTypes = 1
    var weekFormat: WeeksFormats? = null
    var attendanceFormat: AttendanceFormats? = null
}