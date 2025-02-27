package ru.slavapmk.journaltracker.datamodels.schedule

data class ScheduleListLesson(
    val id: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val type: String,
    val auditory: Int,
    val campus: String,
    val name: String,
    val teacher: String
)