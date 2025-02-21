package ru.slavapmk.journaltracker.viewmodels

import androidx.lifecycle.ViewModel

class EditLessonViewModel : ViewModel() {
    var name: String = ""
    var teacher: String = ""
    var cabinet: String = ""
    var campus: String = ""
    var order: String = ""
    var type: String = ""
}