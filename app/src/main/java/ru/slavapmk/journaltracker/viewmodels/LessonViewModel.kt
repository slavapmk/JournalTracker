package ru.slavapmk.journaltracker.viewmodels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journaltracker.datamodels.lessonstudent.LessonStudentListItem
import java.util.Date
import kotlin.properties.Delegates

class LessonViewModel : ViewModel() {
    var index by Delegates.notNull<Int>()
    var name by Delegates.notNull<String>()
    var date by Delegates.notNull<Date>()
    var teacher by Delegates.notNull<String>()
    val students by lazy {
        mutableListOf(
            LessonStudentListItem("Первый", false),
            LessonStudentListItem("Второй", true),
            LessonStudentListItem("Третий", true),
            LessonStudentListItem("Четвёртый", false),
            LessonStudentListItem("Пятый", true),
        )
    }
}