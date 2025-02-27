package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.settings.StudentsFormats
import ru.slavapmk.journalTracker.dataModels.settings.WeeksFormats

class SettingsViewModel : ViewModel() {
    var groupName = ""
    var weekTypes = 1
    lateinit var weekFormat: WeeksFormats
    lateinit var studentFormat: StudentsFormats
}