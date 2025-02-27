package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.lesson.LessonInfo
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem

class LessonViewModel : ViewModel() {
    var info: LessonInfo = LessonInfo(
        123,
        1,
        "Физика",
        "Л",
        "Носков",
        27, 2, 25,
        11, 15,
        12, 30,
        listOf(
            LessonStudentListItem("Первый", false),
            LessonStudentListItem("Второй", true),
            LessonStudentListItem("Третий", true),
            LessonStudentListItem("Четвёртый", false),
            LessonStudentListItem("Пятый", true),
        )
    )
}