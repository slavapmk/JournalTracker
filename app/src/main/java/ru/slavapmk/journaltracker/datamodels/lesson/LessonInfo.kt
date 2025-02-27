package ru.slavapmk.journaltracker.datamodels.lesson

data class LessonInfo(
    val id: Int,
    val index: Int,
    val name: String,
    val type: String,
    val teacher: String,
    val dateDay: Int,
    val dateMonth: Int,
    val dateYear: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val students: List<LessonStudentListItem>
)