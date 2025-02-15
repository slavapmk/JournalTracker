package ru.slavapmk.journaltracker.models

import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var groupName = ""
    var weekTypes = 1
    var weekFormat = ""
    var studentFormat = ""
}