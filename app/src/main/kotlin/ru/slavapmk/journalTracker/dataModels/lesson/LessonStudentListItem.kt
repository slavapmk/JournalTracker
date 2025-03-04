package ru.slavapmk.journalTracker.dataModels.lesson

import ru.slavapmk.journalTracker.ui.lesson.StudentAttendance

data class LessonStudentListItem(
    var id: Int,
    var name: String,
    var attendance: StudentAttendance,
    var description: String
)
