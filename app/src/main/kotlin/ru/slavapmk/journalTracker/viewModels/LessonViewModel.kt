package ru.slavapmk.journalTracker.viewModels

import androidx.lifecycle.ViewModel
import ru.slavapmk.journalTracker.dataModels.lesson.LessonInfo
import ru.slavapmk.journalTracker.dataModels.lesson.LessonStudentListItem
import ru.slavapmk.journalTracker.ui.lesson.StudentAttendance

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
            LessonStudentListItem(0, "Первый", StudentAttendance.VISIT, "rgtebfilhber"),
            LessonStudentListItem(0, "Второй", StudentAttendance.VISIT, "rgtebfilhber"),
            LessonStudentListItem(0, "Третий", StudentAttendance.VISIT, "rgtebfilhber"),
            LessonStudentListItem(0, "Четвёртый", StudentAttendance.VISIT, "rgtebfilhber"),
            LessonStudentListItem(0, "Пятый", StudentAttendance.VISIT, "rgtebfilhber"),
        )
    )
}