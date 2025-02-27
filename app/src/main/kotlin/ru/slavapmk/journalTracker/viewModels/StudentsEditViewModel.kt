package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.studentsEdit.StudentsEditListItem

class StudentsEditViewModel : ViewModel() {
    var studentName = ""
    val studentsList: MutableList<StudentsEditListItem> by lazy {
        mutableListOf(
            StudentsEditListItem("Первый"),
            StudentsEditListItem("Второй"),
            StudentsEditListItem("Третий"),
            StudentsEditListItem("Четвёртый"),
            StudentsEditListItem("Пятый"),
        )
    }
}