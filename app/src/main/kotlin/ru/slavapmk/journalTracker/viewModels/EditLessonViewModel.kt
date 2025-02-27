package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.lessonEdit.LessonEditInfo

class EditLessonViewModel : ViewModel() {
    lateinit var info: LessonEditInfo
}