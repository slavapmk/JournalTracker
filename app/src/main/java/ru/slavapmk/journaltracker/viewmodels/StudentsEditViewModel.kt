package ru.slavapmk.journaltracker.viewmodels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journaltracker.datamodels.editstudents.EditStudentsListItem

class StudentsEditViewModel : ViewModel() {
    var studentName = ""
    val studentsList: MutableList<EditStudentsListItem> by lazy {
        mutableListOf(
            EditStudentsListItem("Первый"),
            EditStudentsListItem("Второй"),
            EditStudentsListItem("Третий"),
            EditStudentsListItem("Четвёртый"),
            EditStudentsListItem("Пятый"),
        )
    }
}