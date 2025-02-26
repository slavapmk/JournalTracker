package ru.slavapmk.journaltracker.viewmodels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journaltracker.datamodels.ListStudentItem

class StudentsEditViewModel : ViewModel() {
    var studentName = ""
    val studentsList: MutableList<ListStudentItem> by lazy {
        mutableListOf(
            ListStudentItem("Первый"),
            ListStudentItem("Второй"),
            ListStudentItem("Третий"),
            ListStudentItem("Четвёртый"),
            ListStudentItem("Пятый"),
        )
    }
}