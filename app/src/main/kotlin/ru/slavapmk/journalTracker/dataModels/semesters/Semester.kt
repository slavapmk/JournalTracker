package ru.slavapmk.journalTracker.dataModels.semesters

data class Semester(
    val startDay: Int,
    val startMonth: Int,
    val startYear: Int,
    val endDay: Int,
    val endMonth: Int,
    val endYear: Int,
)
