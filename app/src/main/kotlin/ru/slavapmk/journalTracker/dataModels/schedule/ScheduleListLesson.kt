package ru.slavapmk.journalTracker.dataModels.schedule

import ru.slavapmk.journalTracker.dataModels.LessonTypeEdit

data class ScheduleListLesson(
    val id: Int,
    val index: Int,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val type: LessonTypeEdit,
    val auditory: Int,
    val campus: String,
    val name: String,
    val teacher: String
)