package ru.slavapmk.journaltracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journaltracker.dataModels.lessonEdit.LessonEditInfo

class EditLessonViewModel : ViewModel() {
    lateinit var info: LessonEditInfo
}