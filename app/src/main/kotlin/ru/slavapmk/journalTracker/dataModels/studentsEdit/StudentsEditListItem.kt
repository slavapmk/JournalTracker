package ru.slavapmk.journalTracker.dataModels.studentsEdit

import ru.slavapmk.journalTracker.ui.lesson.StudentAttendanceLesson

data class StudentsEditListItem(
    val id: Int?,
    val name: String,
    var default: StudentAttendanceLesson?
)
