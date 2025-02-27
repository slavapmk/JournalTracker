package ru.slavapmk.journaltracker.viewmodels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journaltracker.datamodels.editstudent.EditStudentListItem

class StudentsEditViewModel : ViewModel() {
    var studentName = ""
    val studentsList: MutableList<EditStudentListItem> by lazy {
        mutableListOf(
            EditStudentListItem("Первый"),
            EditStudentListItem("Второй"),
            EditStudentListItem("Третий"),
            EditStudentListItem("Четвёртый"),
            EditStudentListItem("Пятый"),
        )
    }
}