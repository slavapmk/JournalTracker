package ru.slavapmk.journalTracker.dataModels.lesson

import ru.slavapmk.journalTracker.ui.lesson.StudentAttendanceLesson

data class LessonStudentListItem(
    var id: Int,
    var studentId: Int,
    var name: String,
    var attendance: StudentAttendanceLesson,
    var description: String? = null
)
